package com.example.iot.data.mqtt.domain

import android.util.Log
import com.example.iot.data.local.device.Device
import com.example.iot.data.local.device.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object DeviceParser {
    fun parseAndLogDevice(
        jsonString: String,
        deviceRepository: DeviceRepository,
        coroutineScope: CoroutineScope,
        brokerId: Int
    ) {
        try {
            val jsonObject = JSONObject(jsonString)

            if (!jsonObject.keys().hasNext()) {
                Log.w("DEVICE", "Empty JSON object received")
                return
            }

            val key = jsonObject.keys().next()
            val deviceJson = jsonObject.optJSONObject(key) ?: run {
                Log.w("DEVICE", "Expected a JSON object for key: $key")
                return
            }

            val ieeeAddr = deviceJson.optString("ieeeAddr") ?: run {
                Log.w("DEVICE", "Missing 'ieeeAddr' field in device data")
                return
            }
            val friendlyName = deviceJson.optString("friendly_name") ?: run {
                Log.w("DEVICE", "Missing 'friendly_name' field in device data")
                return
            }
            val modelId = deviceJson.optString("ModelId") ?: run {
                Log.w("DEVICE", "Missing 'ModelId' field in device data")
                return
            }

            val device = Device.create(
                ieeeAddr = ieeeAddr,
                friendlyName = friendlyName,
                modelId = modelId,
                roomId = null,
                brokerId = brokerId
            )

            Log.i("DEVICE", "Received device: $device")

            coroutineScope.launch {
                val isAdded = deviceRepository.addDeviceIfNotExists(device)
                if (isAdded) {
                    Log.i("DEVICE", "Device added: $device")
                } else {
                    Log.i("DEVICE", "Device already exists: $device")
                }
            }

        } catch (e: Exception) {
            Log.e("DEVICE", "JSON parse error: ${e.message}")
        }
    }
}