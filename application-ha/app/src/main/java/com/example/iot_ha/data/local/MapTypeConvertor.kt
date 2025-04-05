package com.example.iot_ha.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapTypeConvertor {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String? {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, String>? {
        return json?.let {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}

