package com.example.data.repository

import com.example.data.dao.ServerDao
import com.example.data.model.ServerProfile
import com.example.network.PingTester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ServerRepository(private val serverDao: ServerDao) {

    val allServers: Flow<List<ServerProfile>> = serverDao.getAllServers()
    val activeServer: Flow<ServerProfile?> = serverDao.getActiveServer()

    suspend fun getServerById(id: Long): ServerProfile? = withContext(Dispatchers.IO) {
        serverDao.getServerById(id)
    }

    suspend fun insertServer(server: ServerProfile): Long = withContext(Dispatchers.IO) {
        serverDao.insertServer(server)
    }

    suspend fun updateServer(server: ServerProfile) = withContext(Dispatchers.IO) {
        serverDao.updateServer(server)
    }

    suspend fun deleteServer(server: ServerProfile) = withContext(Dispatchers.IO) {
        serverDao.deleteServer(server)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        serverDao.deleteById(id)
    }

    suspend fun setActive(id: Long) = withContext(Dispatchers.IO) {
        serverDao.setActiveServer(id)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        serverDao.toggleFavorite(id, isFavorite)
    }

    suspend fun updatePing(id: Long, pingMs: Int) = withContext(Dispatchers.IO) {
        serverDao.updatePing(id, pingMs)
    }

    suspend fun testServerPing(server: ServerProfile): Int = withContext(Dispatchers.IO) {
        val ping = PingTester.testSocketPing(server.serverAddress, server.serverPort)
        serverDao.updatePing(server.id, ping)
        ping
    }

    suspend fun testAllPings(servers: List<ServerProfile>) = withContext(Dispatchers.IO) {
        coroutineScope {
            val deferreds = servers.map { server ->
                async {
                    val ping = PingTester.testSocketPing(server.serverAddress, server.serverPort)
                    serverDao.updatePing(server.id, ping)
                }
            }
            deferreds.awaitAll()
        }
    }
}
