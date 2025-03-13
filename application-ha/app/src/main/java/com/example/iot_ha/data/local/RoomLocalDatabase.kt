package com.example.iot_ha.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.local.broker.BrokerDao

@Database(
    entities = [Broker::class],
    version = 1,
    exportSchema = false
)
abstract class RoomLocalDatabase : RoomDatabase() {
    abstract fun brokerDAO(): BrokerDao

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