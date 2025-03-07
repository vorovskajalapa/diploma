package com.example.iot.data.mqtt

import android.content.Context
import android.util.Log
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.local.device.Device
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject

class MqttClientHelper private constructor(
    private val context: Context?,
    private val broker: Broker
) {
    private var mqttClient: MqttClient? = null

    companion object {
        private const val CLIENT_ID_PREFIX = "AndroidClient_"

        /**
         * Создает экземпляр MqttClientHelper на основе данных брокера.
         */
        fun create(context: Context?, broker: Broker): MqttClientHelper {
            return MqttClientHelper(context, broker)
        }
    }

    /**
     * Подключается к брокеру MQTT.
     * @return 1 в случае успеха, -1 в случае ошибки.
     */
    fun connect(): Int {
        return try {
            val persistence = MemoryPersistence()
            val brokerUri = "tcp://${broker.serverUri}:${broker.serverPort}"
            val clientId = CLIENT_ID_PREFIX + System.currentTimeMillis()

            mqttClient = MqttClient(brokerUri, clientId, persistence)

            val options = MqttConnectOptions().apply {
                broker.user?.let { userName = it }

                broker.password?.let { password = it.toCharArray() }

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

            // Подписка на топик по умолчанию
            mqttClient?.subscribe("testtopic", 1)
            Log.i("MQTT", "📡 Подписка на testtopic")

            1 // Успех
        } catch (e: MqttException) {
            e.printStackTrace()
            Log.e("MQTT", "❌ Ошибка подключения: ${e.message} (код ${e.reasonCode})")
            -1 // Ошибка
        }
    }

    /**
     * Отправляет сообщение в указанный топик.
     * @param topic Топик, в который отправляется сообщение.
     * @param payload Сообщение для отправки.
     * @param qos Уровень качества обслуживания (QoS).
     */
    fun publishMessage(topic: String, payload: String, qos: Int = 1) {
        try {
            val message = MqttMessage(payload.toByteArray()).apply {
                this.qos = qos
            }
            mqttClient?.publish(topic, message)
            Log.i("MQTT", "📤 Отправлено сообщение в $topic: $payload")
        } catch (e: MqttException) {
            Log.e("MQTT", "❌ Ошибка отправки сообщения: ${e.message}")
        }
    }

    /**
     * Отключается от брокера MQTT.
     */
    fun disconnect() {
        try {
            mqttClient?.disconnect()
            Log.i("MQTT", "🔌 Отключено от брокера")
        } catch (e: MqttException) {
            Log.e("MQTT", "❌ Ошибка отключения: ${e.message}")
        }
    }

    /**
     * Парсит JSON-сообщение и логирует данные устройства.
     * @param jsonString JSON-строка, содержащая данные устройства.
     */
    private fun parseAndLogDevice(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val key = jsonObject.keys().next()
            val deviceJson = jsonObject.getJSONObject(key)

            val ieeeAddr = deviceJson.getString("ieeeAddr")
            val friendlyName = deviceJson.getString("friendly_name")
            val modelId = deviceJson.getString("ModelId")

            val device = Device.create(ieeeAddr, friendlyName, modelId, null, broker.id)
            Log.i("DEVICE", "📡 Получен девайс: $device")
        } catch (e: Exception) {
            Log.e("DEVICE", "❌ Ошибка парсинга JSON: ${e.message}")
        }
    }
}