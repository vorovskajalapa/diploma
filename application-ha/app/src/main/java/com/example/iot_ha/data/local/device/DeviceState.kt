package com.example.iot_ha.data.local.device

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object DeviceState {
    private val _devicesData = MutableStateFlow<Map<Int, Map<String, Any>>>(emptyMap())
    val devicesData: StateFlow<Map<Int, Map<String, Any>>> = _devicesData

    fun updateDeviceData(deviceId: Int, payload: String) {
        val parsedData: Map<String, Any> = parseJson(payload)

        _devicesData.update { currentData ->
            currentData.toMutableMap().apply { this[deviceId] = parsedData }
        }
    }


    private fun parseJson(json: String): Map<String, Any> {
        return try {
            val jsonObject = org.json.JSONObject(json)
            jsonObject.keys().asSequence().associateWith { jsonObject.get(it) }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getDeviceValue(deviceId: Int, key: String): Any? {
        return _devicesData.value[deviceId]?.get(key)
    }
}
