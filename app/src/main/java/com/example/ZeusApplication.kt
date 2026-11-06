package com.example

import android.app.Application
import com.example.data.db.AppDatabase
import com.example.data.model.ServerProfile
import com.example.data.repository.ServerRepository
import com.example.data.settings.AppSettings
import com.example.parser.ConfigParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ZeusApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var serverRepository: ServerRepository
        private set

    lateinit var appSettings: AppSettings
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        serverRepository = ServerRepository(database.serverDao())
        appSettings = AppSettings(this)

        // Ensure database has default server if count is 0
        CoroutineScope(Dispatchers.IO).launch {
            val count = database.serverDao().getCount()
            if (count == 0) {
                val parsed = ConfigParser.parse(AppDatabase.DEFAULT_USER_CONFIG)?.copy(
                    isActive = true,
                    isFavorite = true
                ) ?: ServerProfile(
                    name = "ZEUS | 🇺🇸 | RHECD1YD",
                    protocol = "VLESS",
                    serverAddress = "45.80.108.169",
                    serverPort = 443,
                    uuid = "50414e45-4c5f-5a45-5553-015280c92561",
                    transport = "ws",
                    path = "/stream/PANEL_ZEUS/015280c92561/loc-0",
                    host = "qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev",
                    sni = "qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev",
                    security = "tls",
                    fingerprint = "unsafe",
                    rawLink = AppDatabase.DEFAULT_USER_CONFIG,
                    isActive = true,
                    isFavorite = true
                )
                database.serverDao().insertServer(parsed)
            }
        }
    }
}
