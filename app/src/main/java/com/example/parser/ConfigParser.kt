package com.example.parser

import android.net.Uri
import android.util.Base64
import com.example.data.model.ServerProfile
import org.json.JSONObject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object ConfigParser {

    fun parse(rawInput: String): ServerProfile? {
        val trimmed = rawInput.trim()
        return when {
            trimmed.startsWith("vless://", ignoreCase = true) -> parseVless(trimmed)
            trimmed.startsWith("vmess://", ignoreCase = true) -> parseVmess(trimmed)
            trimmed.startsWith("trojan://", ignoreCase = true) -> parseTrojan(trimmed)
            trimmed.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(trimmed)
            else -> null
        }
    }

    fun parseVless(raw: String): ServerProfile? {
        return try {
            val withoutPrefix = raw.substring(8) // remove "vless://"
            val hashIndex = withoutPrefix.indexOf('#')
            val urlPart: String
            val tag: String
            if (hashIndex != -1) {
                urlPart = withoutPrefix.substring(0, hashIndex)
                tag = try {
                    URLDecoder.decode(withoutPrefix.substring(hashIndex + 1), StandardCharsets.UTF_8.name())
                } catch (e: Exception) {
                    withoutPrefix.substring(hashIndex + 1)
                }
            } else {
                urlPart = withoutPrefix
                tag = "VLESS Server"
            }

            val atIndex = urlPart.indexOf('@')
            if (atIndex == -1) return null
            val uuid = urlPart.substring(0, atIndex)

            val hostAndRest = urlPart.substring(atIndex + 1)
            val questionIndex = hostAndRest.indexOf('?')
            val hostPort = if (questionIndex != -1) hostAndRest.substring(0, questionIndex) else hostAndRest
            val queryString = if (questionIndex != -1) hostAndRest.substring(questionIndex + 1) else ""

            val parts = hostPort.split(":")
            val serverAddress = parts[0]
            val serverPort = parts.getOrNull(1)?.toIntOrNull() ?: 443

            val queryParams = mutableMapOf<String, String>()
            if (queryString.isNotEmpty()) {
                queryString.split("&").forEach { pair ->
                    val kv = pair.split("=")
                    if (kv.isNotEmpty()) {
                        val key = kv[0]
                        val value = if (kv.size > 1) {
                            try {
                                URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name())
                            } catch (e: Exception) {
                                kv[1]
                            }
                        } else ""
                        queryParams[key] = value
                    }
                }
            }

            val transport = queryParams["type"] ?: "tcp"
            val path = queryParams["path"] ?: ""
            val host = queryParams["host"] ?: ""
            val sni = queryParams["sni"] ?: host
            val security = queryParams["security"] ?: "none"
            val fp = queryParams["fp"] ?: "chrome"
            val allowInsecure = queryParams["allowInsecure"] == "1" || queryParams["insecure"] == "1"

            ServerProfile(
                name = tag.ifEmpty { "VLESS-$serverAddress" },
                protocol = "VLESS",
                serverAddress = serverAddress,
                serverPort = serverPort,
                uuid = uuid,
                transport = transport,
                path = path,
                host = host,
                sni = sni,
                security = security,
                fingerprint = fp,
                allowInsecure = allowInsecure,
                rawLink = raw
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseVmess(raw: String): ServerProfile? {
        return try {
            val content = raw.substring(8)
            val decodedBytes = Base64.decode(content, Base64.DEFAULT)
            val jsonStr = String(decodedBytes, StandardCharsets.UTF_8)
            val json = JSONObject(jsonStr)

            val address = json.optString("add", "")
            val port = json.optInt("port", 443)
            val id = json.optString("id", "")
            val net = json.optString("net", "ws")
            val path = json.optString("path", "")
            val host = json.optString("host", "")
            val tls = json.optString("tls", "")
            val ps = json.optString("ps", "VMess Server")
            val sni = json.optString("sni", host)

            ServerProfile(
                name = ps.ifEmpty { "VMess-$address" },
                protocol = "VMess",
                serverAddress = address,
                serverPort = port,
                uuid = id,
                transport = net,
                path = path,
                host = host,
                sni = sni,
                security = if (tls.equals("tls", ignoreCase = true)) "tls" else "none",
                rawLink = raw
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseTrojan(raw: String): ServerProfile? {
        return try {
            val uri = Uri.parse(raw)
            val password = uri.userInfo ?: ""
            val host = uri.host ?: ""
            val port = if (uri.port != -1) uri.port else 443
            val sni = uri.getQueryParameter("sni") ?: host
            val tag = uri.fragment ?: "Trojan-$host"

            ServerProfile(
                name = tag,
                protocol = "Trojan",
                serverAddress = host,
                serverPort = port,
                uuid = password,
                transport = uri.getQueryParameter("type") ?: "tcp",
                path = uri.getQueryParameter("path") ?: "",
                host = uri.getQueryParameter("host") ?: host,
                sni = sni,
                security = "tls",
                rawLink = raw
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseShadowsocks(raw: String): ServerProfile? {
        return try {
            val withoutPrefix = raw.substring(5)
            val hashIndex = withoutPrefix.indexOf('#')
            val tag = if (hashIndex != -1) {
                URLDecoder.decode(withoutPrefix.substring(hashIndex + 1), StandardCharsets.UTF_8.name())
            } else "Shadowsocks Server"
            val corePart = if (hashIndex != -1) withoutPrefix.substring(0, hashIndex) else withoutPrefix

            val atIndex = corePart.indexOf('@')
            if (atIndex != -1) {
                val hostPort = corePart.substring(atIndex + 1).split(":")
                val userInfo = corePart.substring(0, atIndex)
                val methodPass = try {
                    String(Base64.decode(userInfo, Base64.DEFAULT), StandardCharsets.UTF_8)
                } catch (e: Exception) {
                    userInfo
                }
                ServerProfile(
                    name = tag,
                    protocol = "Shadowsocks",
                    serverAddress = hostPort[0],
                    serverPort = hostPort.getOrNull(1)?.toIntOrNull() ?: 8388,
                    uuid = methodPass,
                    transport = "tcp",
                    security = "none",
                    rawLink = raw
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun toShareLink(profile: ServerProfile): String {
        return when (profile.protocol.uppercase()) {
            "VLESS" -> {
                val builder = StringBuilder("vless://")
                builder.append(profile.uuid).append("@").append(profile.serverAddress).append(":").append(profile.serverPort)
                builder.append("?type=").append(profile.transport)
                if (profile.path.isNotEmpty()) builder.append("&path=").append(Uri.encode(profile.path))
                if (profile.host.isNotEmpty()) builder.append("&host=").append(Uri.encode(profile.host))
                if (profile.sni.isNotEmpty()) builder.append("&sni=").append(Uri.encode(profile.sni))
                builder.append("&security=").append(profile.security)
                builder.append("&fp=").append(profile.fingerprint)
                if (profile.allowInsecure) builder.append("&allowInsecure=1")
                builder.append("#").append(Uri.encode(profile.name))
                builder.toString()
            }
            "TROJAN" -> {
                "trojan://${profile.uuid}@${profile.serverAddress}:${profile.serverPort}?security=${profile.security}&sni=${Uri.encode(profile.sni)}#${Uri.encode(profile.name)}"
            }
            "VMESS" -> {
                val json = JSONObject().apply {
                    put("v", "2")
                    put("ps", profile.name)
                    put("add", profile.serverAddress)
                    put("port", profile.serverPort)
                    put("id", profile.uuid)
                    put("aid", 0)
                    put("net", profile.transport)
                    put("type", "none")
                    put("host", profile.host)
                    put("path", profile.path)
                    put("tls", if (profile.security == "tls") "tls" else "")
                    put("sni", profile.sni)
                }
                val encoded = Base64.encodeToString(json.toString().toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
                "vmess://$encoded"
            }
            else -> profile.rawLink.ifEmpty {
                "vless://${profile.uuid}@${profile.serverAddress}:${profile.serverPort}#${Uri.encode(profile.name)}"
            }
        }
    }
}
