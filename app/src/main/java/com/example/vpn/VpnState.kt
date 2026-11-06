package com.example.vpn

import com.example.data.model.ServerProfile

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

data class TrafficStats(
    val uploadSpeedBytesPerSec: Long = 0,
    val downloadSpeedBytesPerSec: Long = 0,
    val totalUploadedBytes: Long = 0,
    val totalDownloadedBytes: Long = 0,
    val connectedDurationSec: Long = 0
) {
    fun formatDuration(): String {
        val hours = connectedDurationSec / 3600
        val minutes = (connectedDurationSec % 3600) / 60
        val seconds = connectedDurationSec % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    companion object {
        fun formatBytes(bytes: Long): String {
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format("%.2f GB", gb)
                mb >= 1.0 -> String.format("%.1f MB", mb)
                kb >= 1.0 -> String.format("%.0f KB", kb)
                else -> "$bytes B"
            }
        }

        fun formatSpeed(bytesPerSec: Long): String {
            val kb = bytesPerSec / 1024.0
            val mb = kb / 1024.0
            return when {
                mb >= 1.0 -> String.format("%.2f MB/s", mb)
                kb >= 1.0 -> String.format("%.1f KB/s", kb)
                else -> "$bytesPerSec B/s"
            }
        }
    }
}
