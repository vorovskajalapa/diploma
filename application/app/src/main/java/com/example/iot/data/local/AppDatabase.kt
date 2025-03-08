package com.example.iot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.local.broker.BrokerDao
import com.example.iot.data.local.device.Device
import com.example.iot.data.local.device.DeviceDAO
import com.example.iot.data.local.deviceConfig.DeviceConfig
import com.example.iot.data.local.deviceConfig.DeviceConfigDAO
import com.example.iot.data.local.deviceConfig.ParameterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Broker::class, Device::class, DeviceConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brokerDao(): BrokerDao
    abstract fun deviceDao(): DeviceDAO
    abstract fun deviceConfigDao(): DeviceConfigDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        suspend fun initializeDeviceConfig(deviceConfigDAO: DeviceConfigDAO) {
            val config = DeviceConfig(
                modelId = "TS011F",
                field = "state",
                type = ParameterType.SWITCH
            )
            deviceConfigDAO.insert(config)
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            initializeDeviceConfig(database.deviceConfigDao())
                        }
                    }
                })

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
