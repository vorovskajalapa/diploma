package com.example.iot_ha.data.local.broker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brokers")
data class Broker(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverUri: String,
    val serverPort: Int,
    val user: String?,
    val password: String?
)

