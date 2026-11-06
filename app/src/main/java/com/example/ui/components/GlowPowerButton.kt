package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnecting
import com.example.ui.theme.StatusDisconnected
import com.example.vpn.ConnectionState

@Composable
fun GlowPowerButton(
    connectionState: ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val accentColor = MaterialTheme.colorScheme.primary
    val (buttonColor, glowColor) = when (connectionState) {
        ConnectionState.CONNECTED -> Pair(StatusConnected, StatusConnected)
        ConnectionState.CONNECTING -> Pair(StatusConnecting, StatusConnecting)
        ConnectionState.DISCONNECTING -> Pair(StatusConnecting, StatusConnecting)
        ConnectionState.DISCONNECTED -> Pair(accentColor, accentColor)
        ConnectionState.ERROR -> Pair(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(240.dp)
            .testTag("glow_power_button_container")
    ) {
        // Animated background radar pulse rings when connected or connecting
        if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
            ) {
                drawCircle(
                    color = glowColor.copy(alpha = pulseAlpha),
                    radius = size.minDimension / 2.3f
                )
            }
        }

        // Concentric outer ring
        Canvas(modifier = Modifier.size(190.dp)) {
            drawCircle(
                color = buttonColor.copy(alpha = 0.15f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Rotating segmented border when connecting
        if (connectionState == ConnectionState.CONNECTING || connectionState == ConnectionState.DISCONNECTING) {
            Canvas(
                modifier = Modifier
                    .size(175.dp)
                    .rotate(rotationAngle)
            ) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, buttonColor, buttonColor)
                    ),
                    startAngle = 0f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Inner glowing core button
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(145.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            buttonColor.copy(alpha = 0.35f),
                            buttonColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, radius = 72.dp),
                    onClick = onClick
                )
                .padding(24.dp)
                .testTag("vpn_power_button")
        ) {
            when (connectionState) {
                ConnectionState.CONNECTED -> {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Connected",
                        tint = StatusConnected,
                        modifier = Modifier.size(64.dp)
                    )
                }
                ConnectionState.CONNECTING, ConnectionState.DISCONNECTING -> {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Connecting",
                        tint = StatusConnecting,
                        modifier = Modifier
                            .size(64.dp)
                            .rotate(rotationAngle)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Connect",
                        tint = buttonColor,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }
}
