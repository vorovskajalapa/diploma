package com.example.iot_ha.data.local.device

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDAO {
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

    @Query("SELECT * FROM devices WHERE roomId = :roomId")
    fun getDevicesByRoomIdFlow(roomId: Int): Flow<List<Device>>

    @Update
    suspend fun updateDevice(device: Device)

    @Query("SELECT COUNT(*) FROM devices WHERE roomId = :roomId")
    suspend fun getDeviceCountForRoom(roomId: Long): Int

    @Query("SELECT * FROM devices")
    fun getAllDevicesFlow(): Flow<List<Device>>

    @Delete
    suspend fun deleteDevice(device: Device)

    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: Int)

}
