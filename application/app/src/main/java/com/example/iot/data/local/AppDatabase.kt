package com.example.iot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.local.broker.BrokerDao
import com.example.iot.data.local.device.Device
import com.example.iot.data.local.device.DeviceDAO
import com.example.iot.data.local.deviceConfig.DeviceConfig
import com.example.iot.data.local.deviceConfig.DeviceConfigDAO


@Database(entities = [Broker::class, Device::class, DeviceConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brokerDao(): BrokerDao
    abstract fun deviceDao(): DeviceDAO
    abstract fun deviceConfigDao(): DeviceConfigDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
