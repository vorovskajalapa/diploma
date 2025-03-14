package com.example.iot_ha.data.local.command

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CommandDAO {

    @Query("SELECT * FROM commands WHERE commandTopic = :commandTopic LIMIT 1")
    suspend fun getCommandByCommandTopic(commandTopic: String): Command?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: Command)
}