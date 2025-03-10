package com.example.iot.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class Room(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)