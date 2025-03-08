package com.example.iot.data.local.device

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: Device)

    @Query("SELECT * FROM devices WHERE brokerId = :brokerId")
    suspend fun getDevicesByBroker(brokerId: Int): List<Device>

    @Query("SELECT * FROM devices WHERE brokerId = :brokerId")
    fun getDevicesByBrokerFlow(brokerId: Int): Flow<List<Device>>

    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<Device>

    @Query("SELECT * FROM devices WHERE ieeeAddr = :ieeeAddr LIMIT 1")
    suspend fun getDeviceByIeeeAddr(ieeeAddr: String): Device?

    @Query("SELECT * FROM devices WHERE id = :deviceId LIMIT 1")
    suspend fun getDeviceById(deviceId: Int): Device // ? был тут

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDevice(device: Device): Long
}
