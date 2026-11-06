package com.example.vpn

import android.content.Context
import android.content.Intent
import com.example.data.model.ServerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnController {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _trafficStats = MutableStateFlow(TrafficStats())
    val trafficStats: StateFlow<TrafficStats> = _trafficStats.asStateFlow()

    private val _activeServer = MutableStateFlow<ServerProfile?>(null)
    val activeServer: StateFlow<ServerProfile?> = _activeServer.asStateFlow()

    fun updateState(state: ConnectionState) {
        _connectionState.value = state
    }

    fun updateStats(stats: TrafficStats) {
        _trafficStats.value = stats
    }

    fun setActiveServer(server: ServerProfile?) {
        _activeServer.value = server
    }

    fun startVpn(context: Context, server: ServerProfile) {
        _activeServer.value = server
        _connectionState.value = ConnectionState.CONNECTING
        val intent = Intent(context, ZeusVpnService::class.java).apply {
            action = ZeusVpnService.ACTION_CONNECT
            putExtra(ZeusVpnService.EXTRA_SERVER_ID, server.id)
            putExtra(ZeusVpnService.EXTRA_SERVER_NAME, server.name)
            putExtra(ZeusVpnService.EXTRA_SERVER_ADDRESS, server.serverAddress)
            putExtra(ZeusVpnService.EXTRA_SERVER_PORT, server.serverPort)
            putExtra(ZeusVpnService.EXTRA_SERVER_PROTOCOL, server.protocol)
            putExtra(ZeusVpnService.EXTRA_SERVER_UUID, server.uuid)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopVpn(context: Context) {
        _connectionState.value = ConnectionState.DISCONNECTING
        val intent = Intent(context, ZeusVpnService::class.java).apply {
            action = ZeusVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }
}
