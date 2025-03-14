package com.example.iot_ha.data.local.command

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandDAO {

    @Query("SELECT * FROM commands WHERE commandTopic = :commandTopic LIMIT 1")
    suspend fun getCommandByCommandTopic(commandTopic: String): Command?


    @Query("SELECT * FROM commands WHERE deviceId = :deviceId AND commandType = 'switch' LIMIT 1")
    suspend fun getSwitchCommandByDeviceId(deviceId: Int): Command?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: Command)

    @Query("SELECT * FROM commands WHERE commandType = :type")
    fun getCommandsByTypeFlow(type: String): Flow<List<Command>>
}