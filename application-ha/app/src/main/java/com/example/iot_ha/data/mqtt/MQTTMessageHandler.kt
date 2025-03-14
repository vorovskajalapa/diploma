package com.example.iot_ha.data.mqtt

import android.util.Log
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.local.device.Device
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.ui.viewmodels.shared.SensorsViewModel
import org.json.JSONObject

class MQTTMessageHandler(
    private val sensorsViewModel: SensorsViewModel,
    private val devicesViewModel: DevicesViewModel,
) {
    fun handleMessage(topic: String, payload: String) {
        Log.i("MQTTHandler", "📩 Обрабатываем сообщение: $payload с топика: $topic")

        when {
            topic.startsWith("zigbee/0x") -> handleDeviceStateMessage(topic, payload)
            topic.startsWith("homeassistant/") -> handleDeviceCommandMessage(topic, payload)
            topic.startsWith("devicelist") -> handleDeviceListMessage(payload)
            else -> Log.i("MQTTHandler", "⚠ Необрабатываемый топик: $topic")
        }
    }

    private fun handleDeviceStateMessage(topic: String, payload: String) {
        sensorsViewModel.test()
    }

    private fun handleDeviceCommandMessage(topic: String, payload: String) {}

    private fun handleDeviceListMessage(payload: String) {
        try {
            val jsonObject = JSONObject(payload)

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
                brokerId = BrokerState.brokerId
            )

            Log.i("DEVICE", "Received device: $device")

            devicesViewModel.addDeviceIfNotExists(device)

        } catch (e: Exception) {
            Log.e("DEVICE", "JSON parse error: ${e.message}")
        }
    }
}

