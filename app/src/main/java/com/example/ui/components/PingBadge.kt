package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PingFast
import com.example.ui.theme.PingMedium
import com.example.ui.theme.PingSlow
import com.example.ui.theme.PingTimeout

@Composable
fun PingBadge(
    pingMs: Int,
    modifier: Modifier = Modifier
) {
    val (dotColor, text) = when {
        pingMs < 0 -> Pair(PingTimeout, "---")
        pingMs >= 9000 -> Pair(PingTimeout, "Timeout")
        pingMs < 150 -> Pair(PingFast, "$pingMs ms")
        pingMs < 300 -> Pair(PingMedium, "$pingMs ms")
        else -> Pair(PingSlow, "$pingMs ms")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(dotColor.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = dotColor
        )
    }
}
