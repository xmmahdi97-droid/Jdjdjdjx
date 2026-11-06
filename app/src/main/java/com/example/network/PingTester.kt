package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object PingTester {

    suspend fun testSocketPing(host: String, port: Int, timeoutMs: Int = 3000): Int = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        var socket: Socket? = null
        try {
            socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            val duration = (System.currentTimeMillis() - startTime).toInt()
            duration.coerceAtLeast(1)
        } catch (e: Exception) {
            9999 // Unreachable or timeout
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {}
        }
    }

    suspend fun testHttpPing(url: String = "https://www.cloudflare.com/cdn-cgi/trace", timeoutMs: Int = 3000): Int = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = timeoutMs
            connection.readTimeout = timeoutMs
            connection.requestMethod = "HEAD"
            connection.connect()
            val code = connection.responseCode
            if (code in 200..399) {
                (System.currentTimeMillis() - startTime).toInt().coerceAtLeast(1)
            } else {
                9999
            }
        } catch (e: Exception) {
            9999
        }
    }
}
