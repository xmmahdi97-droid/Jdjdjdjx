package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.settings.AccentTheme
import com.example.data.settings.ThemeMode

@Composable
fun ZeusVpnTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    accentTheme: AccentTheme = AccentTheme.CYAN,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val (primary, secondary) = when (accentTheme) {
        AccentTheme.CYAN -> Pair(AccentCyanPrimary, AccentCyanSecondary)
        AccentTheme.EMERALD -> Pair(AccentEmeraldPrimary, AccentEmeraldSecondary)
        AccentTheme.PURPLE -> Pair(AccentPurplePrimary, AccentPurpleSecondary)
        AccentTheme.GOLD -> Pair(AccentGoldPrimary, AccentGoldSecondary)
        AccentTheme.CRIMSON -> Pair(AccentCrimsonPrimary, AccentCrimsonSecondary)
    }

    val colorScheme = if (isDark) {
        if (themeMode == ThemeMode.AMOLED) {
            darkColorScheme(
                primary = primary,
                onPrimary = Color.Black,
                primaryContainer = primary.copy(alpha = 0.2f),
                secondary = secondary,
                onSecondary = Color.Black,
                background = AmoledBackground,
                onBackground = CyberDarkTextPrimary,
                surface = AmoledSurface,
                onSurface = CyberDarkTextPrimary,
                surfaceVariant = AmoledCard,
                onSurfaceVariant = CyberDarkTextSecondary,
                outline = AmoledBorder
            )
        } else {
            darkColorScheme(
                primary = primary,
                onPrimary = Color.Black,
                primaryContainer = primary.copy(alpha = 0.2f),
                secondary = secondary,
                onSecondary = Color.Black,
                background = CyberDarkBackground,
                onBackground = CyberDarkTextPrimary,
                surface = CyberDarkSurface,
                onSurface = CyberDarkTextPrimary,
                surfaceVariant = CyberDarkSurfaceVariant,
                onSurfaceVariant = CyberDarkTextSecondary,
                outline = CyberDarkBorder
            )
        }
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.15f),
            secondary = secondary,
            onSecondary = Color.White,
            background = LightBackground,
            onBackground = LightTextPrimary,
            surface = LightSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = LightSurfaceVariant,
            onSurfaceVariant = LightTextSecondary,
            outline = LightBorder
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
