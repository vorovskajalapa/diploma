package com.example.iot.data.mqtt

import android.content.Context
import android.util.Log
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.local.device.Device
import com.example.iot.data.local.device.DeviceRepository
import com.example.iot.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    private val broker: Broker,
    private val deviceRepository: DeviceRepository,
    private val coroutineScope: CoroutineScope
) {
    private var mqttClient: MqttClient? = null
    private var isSubscribeDeviceTopic = false
    private var isCallbackSet = false

    companion object {
        private const val CLIENT_ID_PREFIX = "AndroidClient_"

        @Volatile
        private var instance: MqttClientHelper? = null

        fun getInstance(): MqttClientHelper {
            return instance ?: throw IllegalStateException("MqttClientHelper not initialized")
        }

        fun initialize(
            context: Context?,
            broker: Broker,
            deviceRepository: DeviceRepository,
            coroutineScope: CoroutineScope
        ): MqttClientHelper {
            return instance ?: synchronized(this) {
                instance ?: MqttClientHelper(context, broker, deviceRepository, coroutineScope).apply {
                    connect()
                }.also { instance = it }
            }
        }

        fun reinitialize(
            context: Context?,
            broker: Broker
        ): Boolean {
            return synchronized(this) {
                val currentInstance = instance
                if (currentInstance != null) {
                    val deviceRepository = currentInstance.deviceRepository
                    val coroutineScope = currentInstance.coroutineScope

                    currentInstance.disconnect()
                    instance?.disconnect()

                    instance = MqttClientHelper(context, broker, deviceRepository, coroutineScope).apply {
                        connect()
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    fun connect(): Int {
        return try {
            if (mqttClient?.isConnected == true) {
                Log.i("MQTT", "Already connected")
                return 1
            }

            if (mqttClient == null) {
                val persistence = MemoryPersistence()
                val brokerUri = "tcp://${broker.serverUri}:${broker.serverPort}"
                val clientId = CLIENT_ID_PREFIX + System.currentTimeMillis()
                mqttClient = MqttClient(brokerUri, clientId, persistence)
            }

            val options = MqttConnectOptions().apply {
                broker.user?.let { userName = it }
                broker.password?.let { password = it.toCharArray() }
                isCleanSession = true
                connectionTimeout = 10
                keepAliveInterval = 60
            }

            if (!isCallbackSet) {
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
                isCallbackSet = true
                Log.i("MQTT", "Callback set")
            }

            mqttClient?.connect(options)
            Log.i("MQTT", "Connected successfully")

            if (!isSubscribeDeviceTopic) {
                mqttClient?.subscribe(Constants.DEVICES_TOPIC, 0)
                isSubscribeDeviceTopic = true
                Log.i("MQTT", "Subscribed to testtopic")
            }

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
            isSubscribeDeviceTopic = false
            isCallbackSet = false
        } catch (e: MqttException) {
            Log.e("MQTT", "Disconnect error: ${e.message}")
        }
    }

    private fun parseAndLogDevice(jsonString: String) {
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
                brokerId = broker.id
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