package com.example.parser

import com.example.data.model.ServerProfile
import org.json.JSONArray
import org.json.JSONObject

object XrayConfigGenerator {

    fun generateJson(profile: ServerProfile, dnsServer: String = "1.1.1.1", bypassLan: Boolean = true): String {
        val root = JSONObject()

        // 1. Log
        root.put("log", JSONObject().apply {
            put("loglevel", "warning")
        })

        // 2. Inbounds (Local Socks5 & HTTP for local proxying / TUN)
        val inbounds = JSONArray()
        inbounds.put(JSONObject().apply {
            put("tag", "socks-in")
            put("port", 10808)
            put("listen", "127.0.0.1")
            put("protocol", "socks")
            put("settings", JSONObject().apply {
                put("auth", "noauth")
                put("udp", true)
            })
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })
        inbounds.put(JSONObject().apply {
            put("tag", "http-in")
            put("port", 10809)
            put("listen", "127.0.0.1")
            put("protocol", "http")
            put("settings", JSONObject())
        })
        root.put("inbounds", inbounds)

        // 3. Outbounds
        val outbounds = JSONArray()

        val proxyOutbound = JSONObject().apply {
            put("tag", "proxy")
            put("protocol", profile.protocol.lowercase())

            val settings = JSONObject()
            when (profile.protocol.uppercase()) {
                "VLESS" -> {
                    val vnextArray = JSONArray()
                    val serverObj = JSONObject().apply {
                        put("address", profile.serverAddress)
                        put("port", profile.serverPort)
                        val usersArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("id", profile.uuid)
                                put("encryption", "none")
                                put("level", 0)
                            })
                        }
                        put("users", usersArray)
                    }
                    vnextArray.put(serverObj)
                    settings.put("vnext", vnextArray)
                }
                "VMESS" -> {
                    val vnextArray = JSONArray()
                    val serverObj = JSONObject().apply {
                        put("address", profile.serverAddress)
                        put("port", profile.serverPort)
                        val usersArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("id", profile.uuid)
                                put("alterId", 0)
                                put("security", "auto")
                                put("level", 0)
                            })
                        }
                        put("users", usersArray)
                    }
                    vnextArray.put(serverObj)
                    settings.put("vnext", vnextArray)
                }
                "TROJAN" -> {
                    val serversArray = JSONArray()
                    val serverObj = JSONObject().apply {
                        put("address", profile.serverAddress)
                        put("port", profile.serverPort)
                        put("password", profile.uuid)
                        put("level", 0)
                    }
                    serversArray.put(serverObj)
                    settings.put("servers", serversArray)
                }
                else -> {
                    val serversArray = JSONArray()
                    val serverObj = JSONObject().apply {
                        put("address", profile.serverAddress)
                        put("port", profile.serverPort)
                        put("password", profile.uuid)
                    }
                    serversArray.put(serverObj)
                    settings.put("servers", serversArray)
                }
            }
            put("settings", settings)

            // StreamSettings
            val streamSettings = JSONObject().apply {
                put("network", profile.transport)
                put("security", profile.security)

                if (profile.transport == "ws") {
                    val wsSettings = JSONObject().apply {
                        if (profile.path.isNotEmpty()) put("path", profile.path)
                        if (profile.host.isNotEmpty()) {
                            put("headers", JSONObject().apply {
                                put("Host", profile.host)
                            })
                        }
                    }
                    put("wsSettings", wsSettings)
                } else if (profile.transport == "grpc") {
                    val grpcSettings = JSONObject().apply {
                        put("serviceName", profile.path)
                        put("multiMode", true)
                    }
                    put("grpcSettings", grpcSettings)
                }

                if (profile.security == "tls") {
                    val tlsSettings = JSONObject().apply {
                        if (profile.sni.isNotEmpty()) put("serverName", profile.sni)
                        put("allowInsecure", profile.allowInsecure)
                        put("fingerprint", profile.fingerprint.ifEmpty { "chrome" })
                    }
                    put("tlsSettings", tlsSettings)
                }
            }
            put("streamSettings", streamSettings)
        }
        outbounds.put(proxyOutbound)

        // Direct outbound (freedom)
        outbounds.put(JSONObject().apply {
            put("tag", "direct")
            put("protocol", "freedom")
            put("settings", JSONObject())
        })

        // Blocked outbound (blackhole)
        outbounds.put(JSONObject().apply {
            put("tag", "block")
            put("protocol", "blackhole")
            put("settings", JSONObject().apply {
                put("response", JSONObject().apply { put("type", "none") })
            })
        })
        root.put("outbounds", outbounds)

        // 4. DNS
        root.put("dns", JSONObject().apply {
            val servers = JSONArray().apply {
                put(dnsServer)
                put("https://1.1.1.1/dns-query")
                put("8.8.8.8")
            }
            put("servers", servers)
        })

        // 5. Routing
        val routing = JSONObject().apply {
            put("domainStrategy", "IPIfNonMatch")
            val rules = JSONArray()

            if (bypassLan) {
                rules.put(JSONObject().apply {
                    put("type", "field")
                    put("outboundTag", "direct")
                    put("ip", JSONArray().apply {
                        put("geoip:private")
                    })
                })
            }

            // Route all rest through proxy
            rules.put(JSONObject().apply {
                put("type", "field")
                put("outboundTag", "proxy")
                put("network", "tcp,udp")
            })

            put("rules", rules)
        }
        root.put("routing", routing)

        return root.toString(2)
    }
}
