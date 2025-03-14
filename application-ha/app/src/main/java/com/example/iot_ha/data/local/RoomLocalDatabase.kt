package com.example.iot_ha.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.local.broker.BrokerDAO
import com.example.iot_ha.data.local.command.Command
import com.example.iot_ha.data.local.command.CommandDAO
import com.example.iot_ha.data.local.device.Device
import com.example.iot_ha.data.local.device.DeviceDAO

@Database(
    entities = [Broker::class, Device::class, Command::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MapTypeConvertor::class)
abstract class RoomLocalDatabase : RoomDatabase() {
    abstract fun brokerDAO(): BrokerDAO
    abstract fun deviceDAO(): DeviceDAO
    abstract fun commandDAO(): CommandDAO

    companion object {
        @Volatile
        private var INSTANCE: RoomLocalDatabase? = null

        fun getInstance(context: Context): RoomLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomLocalDatabase::class.java,
                    "room_local_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}