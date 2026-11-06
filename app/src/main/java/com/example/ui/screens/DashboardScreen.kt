package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServerProfile
import com.example.data.settings.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.Strings
import com.example.ui.components.GlowPowerButton
import com.example.ui.components.ImportLinkDialog
import com.example.ui.components.PingBadge
import com.example.ui.components.TrafficGaugeCard
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnecting
import com.example.ui.theme.StatusDisconnected
import com.example.vpn.ConnectionState

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToServers: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val connectionState by viewModel.connectionState.collectAsState()
    val trafficStats by viewModel.trafficStats.collectAsState()
    val activeServer by viewModel.activeServer.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    var showImportDialog by remember { mutableStateOf(false) }

    // Android VpnService prepare launcher
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.toggleVpn(context)
        }
    }

    val onConnectClicked = {
        val vpnIntent = VpnService.prepare(context)
        if (vpnIntent != null) {
            vpnPrepareLauncher.launch(vpnIntent)
        } else {
            viewModel.toggleVpn(context)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Zeus Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = Strings.get("app_title", settings.language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "VLESS • Xray-Core",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Quick Import Link button
            IconButton(
                onClick = { showImportDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Link",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Big Glowing Power Button
        GlowPowerButton(
            connectionState = connectionState,
            onClick = onConnectClicked
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Connection State Label
        val stateText = when (connectionState) {
            ConnectionState.CONNECTED -> Strings.get("status_connected", settings.language)
            ConnectionState.CONNECTING -> Strings.get("btn_connecting", settings.language)
            ConnectionState.DISCONNECTING -> Strings.get("btn_disconnecting", settings.language)
            ConnectionState.DISCONNECTED -> Strings.get("status_disconnected", settings.language)
            ConnectionState.ERROR -> "خطا در برقراری تونل"
        }

        val stateColor = when (connectionState) {
            ConnectionState.CONNECTED -> StatusConnected
            ConnectionState.CONNECTING -> StatusConnecting
            ConnectionState.DISCONNECTING -> StatusConnecting
            ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.onSurfaceVariant
            ConnectionState.ERROR -> MaterialTheme.colorScheme.error
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(stateColor.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(stateColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stateText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = stateColor
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Active Server Selector Card
        activeServer?.let { server ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(onClick = onNavigateToServers)
                    .testTag("active_server_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("active_server", settings.language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = server.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${server.serverAddress}:${server.serverPort} • ${server.transport.uppercase()} ${if (server.security.isNotEmpty()) "+ ${server.security.uppercase()}" else ""}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Ping Badge
                    Box(
                        modifier = Modifier.clickable {
                            viewModel.pingServer(server)
                        }
                    ) {
                        PingBadge(pingMs = server.pingMs)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Switch Server",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } ?: run {
            // No server selected
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToServers)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "هیچ سروری انتخاب نشده است. برای انتخاب سرور لمس کنید.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Real-time Traffic Gauges
        TrafficGaugeCard(
            stats = trafficStats,
            language = settings.language,
            isConnected = connectionState == ConnectionState.CONNECTED
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showImportDialog) {
        ImportLinkDialog(
            language = settings.language,
            onDismiss = { showImportDialog = false },
            onImportSuccess = { newServer ->
                showImportDialog = false
                viewModel.saveServer(newServer)
                viewModel.selectServer(newServer, context)
            }
        )
    }
}
