package com.example.iot_ha.data.mqtt

import android.util.Log
import com.example.iot_ha.data.local.broker.Broker
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

object MQTTClient {
    private var mqttClient: MqttClient? = null
    private var broker: Broker? = null
    private var messageHandler: MQTTMessageHandler? = null

    fun initialize(broker: Broker, handler: MQTTMessageHandler): MQTTClient {
        this.broker = broker
        this.messageHandler = handler
        return this
    }

    fun reinitialize(newBroker: Broker, handler: MQTTMessageHandler): MQTTClient {
        disconnect()
        broker = newBroker
        messageHandler = handler
        return this
    }

    fun getInstance(): MQTTClient {
        return this
    }

    fun connect(): Boolean {
        return try {
            val clientId = MqttClient.generateClientId()
            mqttClient =
                MqttClient("tcp://${broker?.serverUri}:${broker?.serverPort}", clientId, null)

            val options = MqttConnectOptions().apply {
                userName = broker?.user
                password = broker?.password?.toCharArray()
                isAutomaticReconnect = true
                isCleanSession = true
            }

            mqttClient?.connect(options)
            Log.i("MQTT", "✅ Подключение успешно!")
            true
        } catch (e: MqttException) {
            Log.e("MQTT", "Ошибка подключения: ${e.reasonCode} - ${e.message}")
            false
        }
    }

    fun subscribe(topic: String) {
        try {
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e("MQTT", "Соединение потеряно: ${cause?.message}")
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    val payload = message.toString()
                    Log.i("MQTT", "📩 Получено сообщение: $message на топик: $topic")

                    messageHandler?.handleMessage(topic, payload)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.i("MQTT", "Сообщение отправлено")
                }
            })

            mqttClient?.subscribe(topic)
            Log.i("MQTT", "📡 Подписался на $topic")
        } catch (e: MqttException) {
            Log.e("MQTT", "Ошибка подписки: ${e.reasonCode} - ${e.message}")
        }
    }

    fun publish(topic: String, payload: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage(payload.toByteArray()).apply {
                this.qos = qos
                this.isRetained = retained
            }
            mqttClient?.publish(topic, message)
            Log.i("MQTT", "📤 Отправлено сообщение: $payload в топик: $topic")
        } catch (e: MqttException) {
            Log.e("MQTT", "Ошибка отправки сообщения: ${e.reasonCode} - ${e.message}")
        }
    }

    private fun disconnect() {
        try {
            mqttClient?.disconnect()
            Log.i("MQTT", "🔌 Отключен от брокера")
        } catch (e: MqttException) {
            Log.e("MQTT", "Ошибка отключения: ${e.message}")
        }
    }
}
