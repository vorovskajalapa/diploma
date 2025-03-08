package com.example.iot.data.mqtt

import android.content.Context
import android.util.Log
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.local.device.Device
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject

class MqttClientHelper private constructor(
    private val context: Context?,
    private val broker: Broker
) {
    private var mqttClient: MqttClient? = null

    companion object {
        private const val CLIENT_ID_PREFIX = "AndroidClient_"
        @Volatile private var instance: MqttClientHelper? = null

        fun getInstance(): MqttClientHelper {
            return instance ?: throw IllegalStateException("MqttClientHelper not initialized")
        }

        fun initialize(context: Context?, broker: Broker): MqttClientHelper {
            return instance ?: synchronized(this) {
                instance ?: MqttClientHelper(context, broker).apply {
                    connect()
                }.also { instance = it }
            }
        }

        fun reinitialize(context: Context?, broker: Broker): Boolean {
            return synchronized(this) {
                var connectionResult = false
                instance?.disconnect()
                instance = MqttClientHelper(context, broker).apply {
                    connectionResult = connect() == 1
                }
                connectionResult
            }
        }
    }

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
                    Log.e("MQTT", "Connection lost: ${cause.message}")
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    val msgString = message.payload.decodeToString()
                    Log.i("MQTT", "Received message: $msgString")
                    parseAndLogDevice(msgString)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.i("MQTT", "Delivery complete")
                }
            })

            mqttClient?.connect(options)
            Log.i("MQTT", "Connected successfully")
            mqttClient?.subscribe("testtopic", 1)
            1
        } catch (e: MqttException) {
            Log.e("MQTT", "Connection error: ${e.message}")
            -1
        }
    }

    fun publishMessage(topic: String, payload: String, qos: Int = 1) {
        try {
            val message = MqttMessage(payload.toByteArray()).apply { this.qos = qos }
            mqttClient?.publish(topic, message)
            Log.i("MQTT", "Sent to $topic: $payload")
        } catch (e: MqttException) {
            Log.e("MQTT", "Send error: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            Log.i("MQTT", "Disconnected")
        } catch (e: MqttException) {
            Log.e("MQTT", "Disconnect error: ${e.message}")
        }
    }

    private fun parseAndLogDevice(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val key = jsonObject.keys().next()
            val deviceJson = jsonObject.getJSONObject(key)

            val ieeeAddr = deviceJson.getString("ieeeAddr")
            val friendlyName = deviceJson.getString("friendly_name")
            val modelId = deviceJson.getString("ModelId")

            val device = Device.create(ieeeAddr, friendlyName, modelId, null, broker.id)
            Log.i("DEVICE", "Received device: $device")
        } catch (e: Exception) {
            Log.e("DEVICE", "JSON parse error: ${e.message}")
        }
    }
}