package com.example.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zeus_settings")

enum class ThemeMode {
    SYSTEM, DARK, LIGHT, AMOLED
}

enum class AccentTheme {
    CYAN, EMERALD, PURPLE, GOLD, CRIMSON
}

enum class AppLanguage {
    FA, EN
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val accentTheme: AccentTheme = AccentTheme.CYAN,
    val language: AppLanguage = AppLanguage.FA,
    val dnsType: String = "Cloudflare (1.1.1.1)",
    val customDns: String = "",
    val killSwitchEnabled: Boolean = false,
    val bypassLan: Boolean = true,
    val autoConnect: Boolean = false,
    val ipv6Protection: Boolean = true
)

class AppSettings(private val context: Context) {

    companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_ACCENT = stringPreferencesKey("accent_theme")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_DNS_TYPE = stringPreferencesKey("dns_type")
        val KEY_CUSTOM_DNS = stringPreferencesKey("custom_dns")
        val KEY_KILL_SWITCH = booleanPreferencesKey("kill_switch")
        val KEY_BYPASS_LAN = booleanPreferencesKey("bypass_lan")
        val KEY_AUTO_CONNECT = booleanPreferencesKey("auto_connect")
        val KEY_IPV6_PROTECT = booleanPreferencesKey("ipv6_protect")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { pref ->
        UserSettings(
            themeMode = try {
                ThemeMode.valueOf(pref[KEY_THEME] ?: ThemeMode.DARK.name)
            } catch (e: Exception) {
                ThemeMode.DARK
            },
            accentTheme = try {
                AccentTheme.valueOf(pref[KEY_ACCENT] ?: AccentTheme.CYAN.name)
            } catch (e: Exception) {
                AccentTheme.CYAN
            },
            language = try {
                AppLanguage.valueOf(pref[KEY_LANGUAGE] ?: AppLanguage.FA.name)
            } catch (e: Exception) {
                AppLanguage.FA
            },
            dnsType = pref[KEY_DNS_TYPE] ?: "Cloudflare (1.1.1.1)",
            customDns = pref[KEY_CUSTOM_DNS] ?: "",
            killSwitchEnabled = pref[KEY_KILL_SWITCH] ?: false,
            bypassLan = pref[KEY_BYPASS_LAN] ?: true,
            autoConnect = pref[KEY_AUTO_CONNECT] ?: false,
            ipv6Protection = pref[KEY_IPV6_PROTECT] ?: true
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME] = mode.name }
    }

    suspend fun setAccentTheme(accent: AccentTheme) {
        context.dataStore.edit { it[KEY_ACCENT] = accent.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language.name }
    }

    suspend fun setDns(type: String, custom: String = "") {
        context.dataStore.edit {
            it[KEY_DNS_TYPE] = type
            it[KEY_CUSTOM_DNS] = custom
        }
    }

    suspend fun setKillSwitch(enabled: Boolean) {
        context.dataStore.edit { it[KEY_KILL_SWITCH] = enabled }
    }

    suspend fun setBypassLan(enabled: Boolean) {
        context.dataStore.edit { it[KEY_BYPASS_LAN] = enabled }
    }

    suspend fun setAutoConnect(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_CONNECT] = enabled }
    }

    suspend fun setIpv6Protection(enabled: Boolean) {
        context.dataStore.edit { it[KEY_IPV6_PROTECT] = enabled }
    }
}
