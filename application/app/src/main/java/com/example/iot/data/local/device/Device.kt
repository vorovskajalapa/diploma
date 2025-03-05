package com.example.iot.data.local.device

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ieeeAddr: String,
    val friendlyName: String,
    val modelId: String,
    val topic: String,
    val roomId: Long?
) {
    companion object {
        fun create(ieeeAddr: String, friendlyName: String, modelId: String, roomId: Long?): Device {
            val topic = "zigbee/$ieeeAddr"
            return Device(ieeeAddr = ieeeAddr, friendlyName = friendlyName, modelId = modelId, topic = topic, roomId = roomId)
        }
    }
}
