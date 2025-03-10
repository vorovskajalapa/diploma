package com.example.iot.data.local.device.state

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DeviceStateDAO {

    @Query("SELECT * FROM device_state WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getDeviceStateByDeviceId(deviceId: Int): DeviceState?

    @Insert
    suspend fun insertDeviceState(deviceState: DeviceState)

    @Update
    suspend fun updateDeviceState(deviceState: DeviceState)
}

