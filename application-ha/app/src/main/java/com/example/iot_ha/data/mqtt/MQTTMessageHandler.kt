package com.example.iot_ha.data.mqtt

import android.util.Log
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.local.command.Command
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

    private fun handleDeviceCommandMessage(topic: String, payload: String) {
        try {
            val jsonObject = JSONObject(payload)

            val commandTopic =
                jsonObject.optString("command_topic").takeIf { it.isNotBlank() } ?: return
            val payloadOn = jsonObject.optString("payload_on", null)
            val payloadOff = jsonObject.optString("payload_off", null)
            val commandTemplate = jsonObject.optString("command_template", null)

            val options = jsonObject.optJSONArray("options")?.let { array ->
                (0 until array.length()).associate { index ->
                    array.getString(index) to array.getString(
                        index
                    )
                }
            } ?: emptyMap()

            val commandType = extractCommandTypeFromTopic(topic)
            val deviceIeeeAddr = extractIeeeAddrFromTopic(topic)

            if (deviceIeeeAddr != null) {
                devicesViewModel.getDeviceIdByIeeeAddr(deviceIeeeAddr) { deviceId ->
                    if (deviceId != null) {
                        val command = Command(
                            deviceId = deviceId,
                            commandTopic = commandTopic,
                            payloadOn = payloadOn,
                            payloadOff = payloadOff,
                            options = options,
                            commandTemplate = commandTemplate,
                            commandType = commandType
                        )

                        devicesViewModel.addCommandIfNotExists(command)
                        Log.i("MQTTHandler", "📥 Команда сохранена: $command")
                    } else {
                        Log.e("DeviceId", "Device not found for IEEE Address: $deviceIeeeAddr")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MQTTHandler", "Ошибка обработки команды: ${e.message}")
        }
    }

    private fun extractCommandTypeFromTopic(topic: String): String {
        return when {
            topic.contains("/switch/", ignoreCase = true) -> "switch"
            topic.contains("/select/", ignoreCase = true) -> "select"
            topic.contains("/light/", ignoreCase = true) -> "dimmer"
            else -> "unknown"
        }
    }

    private fun extractIeeeAddrFromTopic(topic: String): String? {
        val regex = Regex("0x[0-9A-Fa-f]+")
        return regex.find(topic)?.value
    }

    private fun handleDeviceListMessage(payload: String) {
        try {
            val jsonObject = JSONObject(payload)

            val key = jsonObject.keys().asSequence().firstOrNull() ?: "Нет данных"
            val deviceJson = jsonObject.optJSONObject(key) ?: JSONObject()

            val ieeeAddr = deviceJson.optString("ieeeAddr")
            val friendlyName = deviceJson.getString("friendly_name")
            val modelId = deviceJson.optString("ModelId")

            val device = Device.create(
                ieeeAddr = ieeeAddr,
                friendlyName = friendlyName,
                modelId = modelId,
                roomId = null,
                brokerId = BrokerState.brokerId.value ?: -1
            )

            Log.i("DEVICE", "📥 Получено устройство: $device")

            devicesViewModel.addDeviceIfNotExists(device)

        } catch (e: Exception) {
            Log.e("DEVICE", "Ошибка обработки списка устройств: ${e.message}")
        }
    }
}

