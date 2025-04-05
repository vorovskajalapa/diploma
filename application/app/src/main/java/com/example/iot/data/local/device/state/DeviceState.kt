package com.example.iot.data.local.device.state

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.iot.data.local.device.Device

@Entity(
    tableName = "device_state",
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeviceState(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceId: Int,
    val state: String
)
