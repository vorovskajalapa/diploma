package com.example.iot.data.local.device

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "devices",
    foreignKeys = [
        ForeignKey(
            entity = com.example.iot.data.local.broker.Broker::class,
            parentColumns = ["id"],
            childColumns = ["brokerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ieeeAddr: String,
    val friendlyName: String,
    val modelId: String,
    val topic: String,
    val roomId: Long?,
    val brokerId: Int
) {
    companion object {
        fun create(ieeeAddr: String, friendlyName: String, modelId: String, roomId: Long?, brokerId: Int): Device {
            val topic = "zigbee/0x$ieeeAddr"
            return Device(
                ieeeAddr = "0x$ieeeAddr",
                friendlyName = friendlyName,
                modelId = modelId,
                topic = topic,
                roomId = roomId,
                brokerId = brokerId
            )
        }
    }
}
