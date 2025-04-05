package com.example.iot.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: Room): Long

    @Transaction
    @Query("SELECT * FROM rooms WHERE name = :roomName LIMIT 1")
    suspend fun getRoomByName(roomName: String): Room?

    @Transaction
    @Query(
        """
        SELECT * FROM rooms WHERE name = :roomName LIMIT 1
        """
    )
    suspend fun getRoomWithDevicesByName(roomName: String): RoomWithDevices?
}
