package com.example.iot.data.local.broker

import androidx.room.*

@Dao
interface BrokerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(broker: Broker)

    @Query("SELECT * FROM Broker")
    suspend fun getAllBrokers(): List<Broker>

    @Delete
    suspend fun deleteBroker(broker: Broker)
}
