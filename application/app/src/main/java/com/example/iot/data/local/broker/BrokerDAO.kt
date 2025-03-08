package com.example.iot.data.local.broker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BrokerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(broker: Broker)

    @Query("SELECT * FROM brokers")
    suspend fun getAllBrokers(): List<Broker>

    @Query("SELECT * FROM brokers ORDER BY id DESC LIMIT 1")
    suspend fun getLastBroker(): Broker

    @Delete
    suspend fun deleteBroker(broker: Broker)
}
