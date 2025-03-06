package com.example.iot.data.local.broker

import androidx.room.*

@Dao
interface BrokerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(broker: Broker)

    @Query("SELECT * FROM brokers")
    suspend fun getAllBrokers(): List<Broker>

    @Query("SELECT * FROM brokers LIMIT 1") // todo: get only last
    suspend fun getLastBroker(): Broker

    @Delete
    suspend fun deleteBroker(broker: Broker)
}
