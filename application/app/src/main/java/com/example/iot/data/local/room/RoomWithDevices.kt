package com.example.iot.data.local.room

import androidx.room.Embedded
import androidx.room.Relation
import com.example.iot.data.local.device.Device

data class RoomWithDevices(
    @Embedded val room: Room,
    @Relation(
        parentColumn = "id",
        entityColumn = "roomId"
    )
    val devices: List<Device>
)
