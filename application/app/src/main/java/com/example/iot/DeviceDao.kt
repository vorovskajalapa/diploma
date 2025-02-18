package com.example.iot

import androidx.room.*

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<Device>

    @Insert
    suspend fun insertDevice(device: Device)

    @Update
    suspend fun updateDevice(device: Device)

    @Delete
    suspend fun deleteDevice(device: Device)
}