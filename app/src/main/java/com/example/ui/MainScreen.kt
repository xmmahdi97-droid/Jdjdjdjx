package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.example.data.settings.AppLanguage
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SettingsScreen

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val settings by viewModel.userSettings.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Support Persian RTL layout direction
    val layoutDirection = if (settings.language == AppLanguage.FA) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        // Handle Back button to return to Dashboard if on other tabs
        BackHandler(enabled = selectedTab != 0) {
            selectedTab = 0
        }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    // Tab 0: Dashboard / Connect
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Default.Bolt else Icons.Outlined.Bolt,
                                contentDescription = Strings.get("tab_connect", settings.language)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.get("tab_connect", settings.language),
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Tab 1: Servers
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Default.Dns else Icons.Outlined.Dns,
                                contentDescription = Strings.get("tab_servers", settings.language)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.get("tab_servers", settings.language),
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Tab 2: Settings & Security
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.Security else Icons.Outlined.Security,
                                contentDescription = Strings.get("tab_settings", settings.language)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.get("tab_settings", settings.language),
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { target ->
                    when (target) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToServers = { selectedTab = 1 }
                        )
                        1 -> ServersScreen(viewModel = viewModel)
                        2 -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
