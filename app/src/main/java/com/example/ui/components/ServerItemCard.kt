package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServerProfile
import com.example.data.settings.AppLanguage
import com.example.ui.Strings
import com.example.ui.theme.StatusConnected

@Composable
fun ServerItemCard(
    server: ServerProfile,
    language: AppLanguage,
    onSelect: () -> Unit,
    onPingClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowQr: () -> Unit,
    onInspectXray: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val borderColor = if (server.isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    }

    val cardBg = if (server.isActive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (server.isActive) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onSelect)
            .testTag("server_card_${server.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Active indicator or Protocol badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (server.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (server.isActive) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = server.protocol.take(3),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Host/Port
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (server.isFavorite) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite",
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${server.serverAddress}:${server.serverPort} • ${server.transport.uppercase()} ${if (server.security.isNotEmpty() && server.security != "none") "+ ${server.security.uppercase()}" else ""}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Ping Badge (Clickable to re-ping)
                Box(modifier = Modifier.clickable(onClick = onPingClick)) {
                    PingBadge(pingMs = server.pingMs)
                }

                // More Menu Button
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text(Strings.get("quick_ping_test", language)) },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onPingClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Strings.get("share_qr", language)) },
                            leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onShowQr()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Strings.get("inspect_xray", language)) },
                            leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onInspectXray()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Strings.get("favorite", language)) },
                            leadingIcon = {
                                Icon(
                                    if (server.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleFavorite()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Strings.get("edit", language)) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    Strings.get("delete", language),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
