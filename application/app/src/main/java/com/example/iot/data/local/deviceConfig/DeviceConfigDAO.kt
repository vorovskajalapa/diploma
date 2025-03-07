package com.example.iot.data.local.deviceConfig

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeviceConfigDAO {

    @Insert
    suspend fun insert(deviceConfig: DeviceConfig)

    @Query("SELECT * FROM device_config WHERE modelId = :modelId")
    suspend fun getDeviceConfigByModelId(modelId: String): List<DeviceConfig>?


    @Query("SELECT * FROM device_config WHERE modelId = :modelId AND type = 'SWITCH' LIMIT 1")
    suspend fun getSwitchFieldByModelId(modelId: String): DeviceConfig // был вопрос
}
