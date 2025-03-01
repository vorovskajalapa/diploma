package com.example.iot.data.mqtt

import android.content.Context
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttClientHelper(context: Context?) {
    var mqttClient: MqttClient? = null

    fun connect() {
        try {
            mqttClient = MqttClient(BROKER_URL, CLIENT_ID, MemoryPersistence())
            val options = MqttConnectOptions()
            options.userName = USERNAME
            options.password = PASSWORD.toCharArray()
            options.isCleanSession = true
            options.connectionTimeout = 10
            options.keepAliveInterval = 60

            mqttClient!!.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable) {
                    println("❌ MQTT: Соединение потеряно: " + cause.message)
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    println("📩 MQTT: Получено сообщение от $topic: $message")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    println("✅ MQTT: Доставка завершена")
                }
            })

            mqttClient!!.connect(options)
            println("✅ MQTT: Подключение успешно")
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ MQTT: Ошибка подключения: " + e.message)
        }
    }


    fun disconnect() {
        try {
            if (mqttClient != null) {
                mqttClient!!.disconnect()
                println("🔌 MQTT: Отключено")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val BROKER_URL = "tcp://m1.wqtt.ru:11918"
        private const val USERNAME = "u_YJ3M6K"
        private const val PASSWORD = "2CQDZ8sa"
        private val CLIENT_ID = "AndroidClient_" + System.currentTimeMillis()
        private const val PUBLISH_TOPIC = "test_publish"
    }
}