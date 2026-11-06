package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServerProfile
import com.example.ui.MainViewModel
import com.example.ui.Strings
import com.example.ui.components.AddEditServerDialog
import com.example.ui.components.ImportLinkDialog
import com.example.ui.components.QrDialog
import com.example.ui.components.ServerItemCard
import com.example.ui.components.XrayConfigDialog

@Composable
fun ServersScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val servers by viewModel.allServers.collectAsState()
    val activeServer by viewModel.activeServer.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedServerForEdit by remember { mutableStateOf<ServerProfile?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var serverForQr by remember { mutableStateOf<ServerProfile?>(null) }
    var serverForXray by remember { mutableStateOf<ServerProfile?>(null) }

    val filteredServers = remember(servers, searchQuery) {
        if (searchQuery.isBlank()) servers
        else servers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.serverAddress.contains(searchQuery, ignoreCase = true) ||
            it.protocol.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.get("tab_servers", settings.language),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Ping All Button
                OutlinedButton(
                    onClick = { viewModel.pingAllServers() },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkPing,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Strings.get("test_all_pings", settings.language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Add Server Button
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Strings.get("add_server", settings.language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search and Import Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(Strings.get("search_servers", settings.language), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )

            IconButton(
                onClick = { showImportDialog = true },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Import link",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Servers List
        if (filteredServers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "هیچ سروری یافت نشد. می‌توانید با دکمه بالا سرور جدید اضافه کنید.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredServers, key = { it.id }) { server ->
                    ServerItemCard(
                        server = server,
                        language = settings.language,
                        onSelect = {
                            viewModel.selectServer(server, context)
                        },
                        onPingClick = {
                            viewModel.pingServer(server)
                        },
                        onEdit = {
                            selectedServerForEdit = server
                        },
                        onDelete = {
                            viewModel.deleteServer(server)
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavorite(server)
                        },
                        onShowQr = {
                            serverForQr = server
                        },
                        onInspectXray = {
                            serverForXray = server
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddEditServerDialog(
            initialServer = null,
            language = settings.language,
            onDismiss = { showAddDialog = false },
            onSave = { newServer ->
                showAddDialog = false
                viewModel.saveServer(newServer)
            }
        )
    }

    selectedServerForEdit?.let { editServer ->
        AddEditServerDialog(
            initialServer = editServer,
            language = settings.language,
            onDismiss = { selectedServerForEdit = null },
            onSave = { updatedServer ->
                selectedServerForEdit = null
                viewModel.saveServer(updatedServer)
            }
        )
    }

    if (showImportDialog) {
        ImportLinkDialog(
            language = settings.language,
            onDismiss = { showImportDialog = false },
            onImportSuccess = { imported ->
                showImportDialog = false
                viewModel.saveServer(imported)
            }
        )
    }

    serverForQr?.let { qrServer ->
        QrDialog(
            server = qrServer,
            language = settings.language,
            onDismiss = { serverForQr = null }
        )
    }

    serverForXray?.let { xrayServer ->
        XrayConfigDialog(
            server = xrayServer,
            language = settings.language,
            onDismiss = { serverForXray = null }
        )
    }
}
