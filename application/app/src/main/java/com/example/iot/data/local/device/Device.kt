package com.example.iot.data.local.device

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val friendlyName: String,
    val topic: String,
    val roomId: Long?
)
