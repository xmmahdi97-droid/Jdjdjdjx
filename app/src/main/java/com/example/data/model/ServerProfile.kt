package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val protocol: String = "VLESS", // VLESS, VMess, Trojan, Shadowsocks
    val serverAddress: String,
    val serverPort: Int = 443,
    val uuid: String,
    val transport: String = "ws", // ws, tcp, grpc
    val path: String = "",
    val host: String = "",
    val sni: String = "",
    val security: String = "tls", // tls, reality, none
    val fingerprint: String = "chrome",
    val allowInsecure: Boolean = false,
    val rawLink: String = "",
    val pingMs: Int = -1, // -1: untested, 9999: timeout, >0: latency in ms
    val isActive: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
