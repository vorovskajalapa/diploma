package com.example.iot_ha.data.local.command

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.iot_ha.data.local.MapTypeConvertor
import com.example.iot_ha.data.local.device.Device

@Entity(
    tableName = "commands",
    foreignKeys = [ForeignKey(
        entity = Device::class,
        parentColumns = ["id"],
        childColumns = ["deviceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Command(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceId: Int,

    val commandTopic: String,

    // for switch commands
    val payloadOn: String?,
    val payloadOff: String?,

    // for select commands
    @TypeConverters(MapTypeConvertor::class) val options: Map<String, String>?,
    val commandTemplate: String?,


    val commandType: String,
)
