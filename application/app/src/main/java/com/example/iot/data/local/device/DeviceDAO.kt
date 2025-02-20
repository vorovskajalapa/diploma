package com.example.iot.data.local.device

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface DeviceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: Device)
}