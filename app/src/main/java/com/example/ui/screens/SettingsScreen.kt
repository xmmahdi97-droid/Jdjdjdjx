package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.AccentTheme
import com.example.data.settings.AppLanguage
import com.example.data.settings.ThemeMode
import com.example.ui.MainViewModel
import com.example.ui.Strings
import com.example.ui.theme.AccentCrimsonPrimary
import com.example.ui.theme.AccentCyanPrimary
import com.example.ui.theme.AccentEmeraldPrimary
import com.example.ui.theme.AccentGoldPrimary
import com.example.ui.theme.AccentPurplePrimary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsState()

    var dnsMenuExpanded by remember { mutableStateOf(false) }
    val dnsOptions = listOf(
        "Cloudflare (1.1.1.1)",
        "Google (8.8.8.8)",
        "AdGuard (94.140.14.14)",
        "Quad9 (9.9.9.9)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = Strings.get("tab_settings", settings.language),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 1. Appearance Section
        SettingsCard(
            title = Strings.get("theme_mode", settings.language),
            icon = Icons.Default.Palette
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Theme Mode Selectors (Dark, Light, AMOLED, System)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val modes = listOf(
                        Pair(ThemeMode.DARK, Strings.get("theme_dark", settings.language)),
                        Pair(ThemeMode.LIGHT, Strings.get("theme_light", settings.language)),
                        Pair(ThemeMode.AMOLED, Strings.get("theme_amoled", settings.language)),
                        Pair(ThemeMode.SYSTEM, Strings.get("theme_system", settings.language))
                    )
                    modes.forEach { (mode, label) ->
                        val isSelected = settings.themeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Accent Color Picker
                Text(
                    text = Strings.get("accent_color", settings.language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val accents = listOf(
                        Pair(AccentTheme.CYAN, AccentCyanPrimary),
                        Pair(AccentTheme.EMERALD, AccentEmeraldPrimary),
                        Pair(AccentTheme.PURPLE, AccentPurplePrimary),
                        Pair(AccentTheme.GOLD, AccentGoldPrimary),
                        Pair(AccentTheme.CRIMSON, AccentCrimsonPrimary)
                    )

                    accents.forEach { (accent, color) ->
                        val isSelected = settings.accentTheme == accent
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { viewModel.setAccentTheme(accent) }
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Language Selector
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("language", settings.language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LanguageBadge(
                            text = "فارسی",
                            isSelected = settings.language == AppLanguage.FA,
                            onClick = { viewModel.setLanguage(AppLanguage.FA) }
                        )
                        LanguageBadge(
                            text = "English",
                            isSelected = settings.language == AppLanguage.EN,
                            onClick = { viewModel.setLanguage(AppLanguage.EN) }
                        )
                    }
                }
            }
        }

        // 2. Security & Privacy Protocols
        SettingsCard(
            title = "پروتکل‌های امنیتی و حریم خصوصی",
            icon = Icons.Default.Security
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // DNS Server Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("dns_title", settings.language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = settings.dnsType,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { dnsMenuExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "تغییر DNS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = dnsMenuExpanded,
                            onDismissRequest = { dnsMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            dnsOptions.forEach { dns ->
                                DropdownMenuItem(
                                    text = { Text(dns) },
                                    onClick = {
                                        dnsMenuExpanded = false
                                        viewModel.setDns(dns)
                                    }
                                )
                            }
                        }
                    }
                }

                // Kill Switch
                SwitchRow(
                    title = Strings.get("kill_switch_title", settings.language),
                    subtitle = Strings.get("kill_switch_desc", settings.language),
                    checked = settings.killSwitchEnabled,
                    onCheckedChange = { viewModel.setKillSwitch(it) }
                )

                // Bypass LAN
                SwitchRow(
                    title = Strings.get("bypass_lan_title", settings.language),
                    subtitle = Strings.get("bypass_lan_desc", settings.language),
                    checked = settings.bypassLan,
                    onCheckedChange = { viewModel.setBypassLan(it) }
                )

                // IPv6 Protection
                SwitchRow(
                    title = Strings.get("ipv6_title", settings.language),
                    subtitle = "جلوگیری از فاش شدن آدرس IPv6 واقعی شما در سراسر وب",
                    checked = settings.ipv6Protection,
                    onCheckedChange = { viewModel.setIpv6Protection(it) }
                )

                // Auto Connect
                SwitchRow(
                    title = Strings.get("auto_connect_title", settings.language),
                    subtitle = "برقراری خودکار ارتباط در هنگام اجرای برنامه",
                    checked = settings.autoConnect,
                    onCheckedChange = { viewModel.setAutoConnect(it) }
                )
            }
        }

        // 3. Engine & Core Info Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "موتور Xray-core و هسته اتصال",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "نسخه موتور: Xray-core v25.x (مبتنی بر https://github.com/xmmahdi97-droid/Xray-core)\nپروتکل‌های فعال: VLESS (XTLS/WS/gRPC), VMess, Trojan, Shadowsocks با قابلیت رمزنگاری TLS و SNI هوشمند.",
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun LanguageBadge(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
