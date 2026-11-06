package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ServerDao
import com.example.data.model.ServerProfile
import com.example.parser.ConfigParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ServerProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        const val DEFAULT_USER_CONFIG = "vless://50414e45-4c5f-5a45-5553-015280c92561@45.80.108.169:443?path=%2Fstream%2FPANEL_ZEUS%2F015280c92561%2Floc-0&security=tls&encryption=none&host=qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev&fp=unsafe&type=ws&allowInsecure=0&sni=qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev#ZEUS%20%7C%20%F0%9F%87%BA%F0%9F%87%B8%20%7C%20RHECD1YD"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zeus_vpn_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default servers
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).serverDao()
                            val defaultServer = ConfigParser.parse(DEFAULT_USER_CONFIG)?.copy(
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
                                rawLink = DEFAULT_USER_CONFIG,
                                isActive = true,
                                isFavorite = true
                            )

                            val backupDe = ServerProfile(
                                name = "ZEUS | 🇩🇪 Frankfurt Turbo",
                                protocol = "VLESS",
                                serverAddress = "172.67.180.20",
                                serverPort = 443,
                                uuid = "50414e45-4c5f-5a45-5553-015280c92561",
                                transport = "ws",
                                path = "/stream/PANEL_ZEUS/015280c92561/loc-1",
                                host = "qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev",
                                sni = "qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev",
                                security = "tls",
                                fingerprint = "chrome",
                                rawLink = "vless://50414e45-4c5f-5a45-5553-015280c92561@172.67.180.20:443?type=ws&security=tls#ZEUS%20%7C%20%F0%9F%87%A9%F0%9F%87%AA%20Frankfurt",
                                isActive = false
                            )

                            val backupFi = ServerProfile(
                                name = "ZEUS | 🇫🇮 Helsinki Low-Ping",
                                protocol = "Trojan",
                                serverAddress = "104.21.45.19",
                                serverPort = 443,
                                uuid = "50414e45-4c5f-5a45-5553-015280c92561",
                                transport = "grpc",
                                path = "zeus-grpc",
                                host = "qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev",
                                sni = "qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev",
                                security = "tls",
                                rawLink = "trojan://50414e45-4c5f-5a45-5553-015280c92561@104.21.45.19:443?security=tls#ZEUS%20%7C%20%F0%9F%87%AB%F0%9F%87%AE%20Helsinki",
                                isActive = false
                            )

                            dao.insertAll(listOf(defaultServer, backupDe, backupFi))
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
