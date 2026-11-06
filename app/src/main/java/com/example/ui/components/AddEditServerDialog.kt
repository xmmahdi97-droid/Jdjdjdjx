package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServerProfile
import com.example.data.settings.AppLanguage
import com.example.ui.Strings

@Composable
fun AddEditServerDialog(
    initialServer: ServerProfile? = null,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (ServerProfile) -> Unit
) {
    var name by remember { mutableStateOf(initialServer?.name ?: "") }
    var protocol by remember { mutableStateOf(initialServer?.protocol ?: "VLESS") }
    var address by remember { mutableStateOf(initialServer?.serverAddress ?: "") }
    var port by remember { mutableStateOf((initialServer?.serverPort ?: 443).toString()) }
    var uuid by remember { mutableStateOf(initialServer?.uuid ?: "") }
    var transport by remember { mutableStateOf(initialServer?.transport ?: "ws") }
    var path by remember { mutableStateOf(initialServer?.path ?: "") }
    var host by remember { mutableStateOf(initialServer?.host ?: "") }
    var sni by remember { mutableStateOf(initialServer?.sni ?: "") }
    var security by remember { mutableStateOf(initialServer?.security ?: "tls") }
    var fingerprint by remember { mutableStateOf(initialServer?.fingerprint ?: "chrome") }

    var errorMessage by remember { mutableStateOf("") }

    val protocols = listOf("VLESS", "VMess", "Trojan", "Shadowsocks")
    val transports = listOf("ws", "tcp", "grpc")
    val securities = listOf("tls", "reality", "none")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialServer == null) Strings.get("add_server", language) else Strings.get("edit", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Protocol Selector
                Text(
                    text = Strings.get("protocol", language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    protocols.forEach { item ->
                        val selected = protocol.equals(item, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { protocol = item }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام سرور (Server Name)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Server Address and Port
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(Strings.get("address", language)) },
                        singleLine = true,
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it.filter { c -> c.isDigit() } },
                        label = { Text(Strings.get("port", language)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // UUID / Password
                OutlinedTextField(
                    value = uuid,
                    onValueChange = { uuid = it },
                    label = { Text(Strings.get("uuid", language)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Transport selector
                Text(
                    text = Strings.get("transport", language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    transports.forEach { item ->
                        val selected = transport.equals(item, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { transport = item }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Path & Host Header (for WS)
                if (transport == "ws" || transport == "grpc") {
                    OutlinedTextField(
                        value = path,
                        onValueChange = { path = it },
                        label = { Text(Strings.get("path", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text(Strings.get("host", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Security & SNI
                Text(
                    text = Strings.get("security", language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    securities.forEach { item ->
                        val selected = security.equals(item, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { security = item }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (security != "none") {
                    OutlinedTextField(
                        value = sni,
                        onValueChange = { sni = it },
                        label = { Text(Strings.get("sni", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (address.isBlank()) {
                        errorMessage = "آدرس سرور الزامی است"
                        return@Button
                    }
                    if (uuid.isBlank()) {
                        errorMessage = "شناسه کاربری (UUID) الزامی است"
                        return@Button
                    }
                    val portNum = port.toIntOrNull() ?: 443
                    val savedServer = ServerProfile(
                        id = initialServer?.id ?: 0,
                        name = name.ifBlank { "$protocol Server - $address" },
                        protocol = protocol,
                        serverAddress = address.trim(),
                        serverPort = portNum,
                        uuid = uuid.trim(),
                        transport = transport,
                        path = path.trim(),
                        host = host.trim(),
                        sni = sni.trim().ifEmpty { host.trim() },
                        security = security,
                        fingerprint = fingerprint,
                        isActive = initialServer?.isActive ?: false,
                        isFavorite = initialServer?.isFavorite ?: false
                    )
                    onSave(savedServer)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Strings.get("save", language), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Strings.get("cancel", language))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
