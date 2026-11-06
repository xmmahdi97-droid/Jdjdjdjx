package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ServerProfile
import com.example.data.repository.ServerRepository
import com.example.data.settings.AccentTheme
import com.example.data.settings.AppLanguage
import com.example.data.settings.AppSettings
import com.example.data.settings.ThemeMode
import com.example.data.settings.UserSettings
import com.example.vpn.ConnectionState
import com.example.vpn.TrafficStats
import com.example.vpn.VpnController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ServerRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    val allServers: StateFlow<List<ServerProfile>> = repository.allServers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeServer: StateFlow<ServerProfile?> = repository.activeServer
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val connectionState: StateFlow<ConnectionState> = VpnController.connectionState
    val trafficStats: StateFlow<TrafficStats> = VpnController.trafficStats

    val userSettings: StateFlow<UserSettings> = appSettings.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    fun toggleVpn(context: Context) {
        val current = connectionState.value
        val server = activeServer.value
        if (current == ConnectionState.CONNECTED || current == ConnectionState.CONNECTING) {
            VpnController.stopVpn(context)
        } else {
            if (server != null) {
                VpnController.startVpn(context, server)
            }
        }
    }

    fun selectServer(server: ServerProfile, context: Context? = null) {
        viewModelScope.launch {
            repository.setActive(server.id)
            // If already connected, reconnect to the newly selected server
            if (connectionState.value == ConnectionState.CONNECTED && context != null) {
                VpnController.stopVpn(context)
                VpnController.startVpn(context, server)
            }
        }
    }

    fun pingServer(server: ServerProfile) {
        viewModelScope.launch {
            repository.testServerPing(server)
        }
    }

    fun pingAllServers() {
        viewModelScope.launch {
            repository.testAllPings(allServers.value)
        }
    }

    fun saveServer(server: ServerProfile) {
        viewModelScope.launch {
            if (server.id == 0L) {
                val newId = repository.insertServer(server)
                if (allServers.value.isEmpty()) {
                    repository.setActive(newId)
                }
            } else {
                repository.updateServer(server)
            }
        }
    }

    fun deleteServer(server: ServerProfile) {
        viewModelScope.launch {
            repository.deleteServer(server)
        }
    }

    fun toggleFavorite(server: ServerProfile) {
        viewModelScope.launch {
            repository.toggleFavorite(server.id, !server.isFavorite)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { appSettings.setThemeMode(mode) }
    }

    fun setAccentTheme(accent: AccentTheme) {
        viewModelScope.launch { appSettings.setAccentTheme(accent) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { appSettings.setLanguage(language) }
    }

    fun setDns(type: String, custom: String = "") {
        viewModelScope.launch { appSettings.setDns(type, custom) }
    }

    fun setKillSwitch(enabled: Boolean) {
        viewModelScope.launch { appSettings.setKillSwitch(enabled) }
    }

    fun setBypassLan(enabled: Boolean) {
        viewModelScope.launch { appSettings.setBypassLan(enabled) }
    }

    fun setAutoConnect(enabled: Boolean) {
        viewModelScope.launch { appSettings.setAutoConnect(enabled) }
    }

    fun setIpv6Protection(enabled: Boolean) {
        viewModelScope.launch { appSettings.setIpv6Protection(enabled) }
    }
}

class MainViewModelFactory(
    private val repository: ServerRepository,
    private val appSettings: AppSettings
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, appSettings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
