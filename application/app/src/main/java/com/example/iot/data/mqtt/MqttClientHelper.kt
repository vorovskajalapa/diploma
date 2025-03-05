package com.example.iot.data.mqtt

import android.content.Context
import android.util.Log
import com.example.iot.data.local.device.Device
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject

class MqttClientHelper(context: Context?) {
    private var mqttClient: MqttClient? = null

    fun parseAndLogDevice(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val key = jsonObject.keys().next()
            val deviceJson = jsonObject.getJSONObject(key)

            val ieeeAddr = deviceJson.getString("ieeeAddr")
            val friendlyName = deviceJson.getString("friendly_name")
            val modelId = deviceJson.getString("ModelId")

            val device = Device.create(ieeeAddr, friendlyName, modelId, null)

            Log.i("DEVICE", "📡 Получен девайс: $device")
        } catch (e: Exception) {
            Log.e("DEVICE", "❌ Ошибка парсинга JSON: ${e.message}")
        }
    }

    fun connect(): Int {
        try {
            val persistence = MemoryPersistence()
            mqttClient = MqttClient(BROKER_URL, CLIENT_ID, persistence)

            val options = MqttConnectOptions().apply {
                userName = USERNAME
                password = PASSWORD.toCharArray()
                isCleanSession = true
                connectionTimeout = 10
                keepAliveInterval = 60
            }

            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable) {
                    Log.e("MQTT", "Соединение потеряно: ${cause.message}")
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    val msgString = message.payload.decodeToString()
                    Log.i("MQTT", "📩 Получено сообщение: $msgString")

                    parseAndLogDevice(msgString)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.i("MQTT", "✅ Доставка завершена")
                }
            })

            mqttClient?.connect(options)
            Log.i("MQTT", "✅ Подключение успешно")

            mqttClient?.subscribe("testtopic", 1) // QoS 1 - гарантированная доставка
            Log.i("MQTT", "📡 Подписка на testtopic")

        } catch (e: MqttException) {
            e.printStackTrace()
            Log.e("MQTT", "❌ Ошибка подключения: ${e.message} (код ${e.reasonCode})")
            return -1;
        }

        return 1;
    }


    fun disconnect() {
        try {
            mqttClient?.disconnect()
            println("🔌 MQTT: Отключено")
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val BROKER_URL = "tcp://m1.wqtt.ru:13058"
        private const val USERNAME = "u_Y6UCHR"
        private const val PASSWORD = "Iv496upx"
        private val CLIENT_ID = "AndroidClient_" + System.currentTimeMillis()
    }
}
