package com.example.iot.data.local.device.state

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceStateDAO {

    @Query("SELECT * FROM device_state WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getDeviceStateByDeviceId(deviceId: Int): DeviceState?

    @Query("SELECT * FROM device_state WHERE deviceId = :deviceId")
    fun getDevicesStateByDeviceId(deviceId: Int): Flow<DeviceState?>

    @Insert
    suspend fun insertDeviceState(deviceState: DeviceState)

    @Update
    suspend fun updateDeviceState(deviceState: DeviceState)

    @Query("SELECT state FROM device_state WHERE deviceId = :deviceId")
    suspend fun getDeviceState(deviceId: Int): String?
}

