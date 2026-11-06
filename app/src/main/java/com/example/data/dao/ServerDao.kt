package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ServerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

    @Query("SELECT * FROM servers ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllServers(): Flow<List<ServerProfile>>

    @Query("SELECT * FROM servers WHERE isActive = 1 LIMIT 1")
    fun getActiveServer(): Flow<ServerProfile?>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerById(id: Long): ServerProfile?

    @Query("SELECT COUNT(*) FROM servers")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerProfile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(servers: List<ServerProfile>)

    @Update
    suspend fun updateServer(server: ServerProfile)

    @Delete
    suspend fun deleteServer(server: ServerProfile)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE servers SET pingMs = :pingMs WHERE id = :id")
    suspend fun updatePing(id: Long, pingMs: Int)

    @Query("UPDATE servers SET isActive = 0")
    suspend fun clearAllActive()

    @Query("UPDATE servers SET isActive = 1 WHERE id = :id")
    suspend fun setOneActive(id: Long)

    @Transaction
    suspend fun setActiveServer(id: Long) {
        clearAllActive()
        setOneActive(id)
    }

    @Query("UPDATE servers SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
