package com.example.iot.data.local.device

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: Device)

    @Query("SELECT * FROM devices WHERE ieeeAddr = :ieeeAddr LIMIT 1")
    suspend fun getDeviceByIeeeAddr(ieeeAddr: String): Device?
}
