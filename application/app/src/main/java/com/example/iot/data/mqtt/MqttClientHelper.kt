package com.example.iot.data.mqtt

import android.content.Context
import android.util.Log
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.local.device.DeviceRepository
import com.example.iot.data.local.device.state.DeviceState
import com.example.iot.data.mqtt.domain.DeviceParser
import com.example.iot.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttClientHelper private constructor(
    private val context: Context?,
    private val broker: Broker,
    private val deviceRepository: DeviceRepository,
    private val coroutineScope: CoroutineScope,
    private val appDatabase: AppDatabase
) {
    private var mqttClient: MqttClient? = null
    private var isSubscribeDeviceTopic = false
    private var isCallbackSet = false

    private val subscribedTopics = mutableSetOf<String>()

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
            coroutineScope: CoroutineScope,
            appDatabase: AppDatabase
        ): MqttClientHelper {
            return instance ?: synchronized(this) {
                instance ?: MqttClientHelper(
                    context,
                    broker,
                    deviceRepository,
                    coroutineScope,
                    appDatabase
                ).apply {
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
                    val appDatabase = currentInstance.appDatabase

                    currentInstance.disconnect()
                    instance?.disconnect()

                    instance =
                        MqttClientHelper(
                            context,
                            broker,
                            deviceRepository,
                            coroutineScope,
                            appDatabase
                        ).apply {
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

                        when {
                            topic == Constants.DEVICES_TOPIC -> {
                                Log.i("MQTT", "Processing testtopic")
                                DeviceParser.parseAndLogDevice(
                                    msgString,
                                    deviceRepository,
                                    coroutineScope,
                                    broker.id
                                )
                            }

                            topic.startsWith("zigbee/") -> {
                                val ieeeAddr = topic.substring("zigbee/".length)
                                Log.i("MQTT", "Processing zigbee topic for ieeeAddr: $ieeeAddr")

                                val jsonPayload = message.payload.decodeToString()

                                coroutineScope.launch {
                                    val device =
                                        appDatabase.deviceDao().getDeviceByIeeeAddr(ieeeAddr)
                                    if (device == null) {
                                        Log.w(
                                            "MQTT",
                                            "Device with IEEE Address $ieeeAddr not found in database"
                                        )
                                        return@launch
                                    }

                                    val existingState = appDatabase.deviceStateDao()
                                        .getDeviceStateByDeviceId(device.id)

                                    if (existingState != null) {
                                        val updatedState = existingState.copy(state = jsonPayload)
                                        appDatabase.deviceStateDao().updateDeviceState(updatedState)
                                        Log.i("MQTT", "Updated state for device ${device.id}")
                                    } else {
                                        val newState =
                                            DeviceState(deviceId = device.id, state = jsonPayload)
                                        appDatabase.deviceStateDao().insertDeviceState(newState)
                                        Log.i("MQTT", "Inserted new state for device ${device.id}")
                                    }
                                }
                            }

                            else -> {
                                Log.w("MQTT", "Unknown topic: $topic")
                            }
                        }
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

            coroutineScope.launch {
                while (true) {
                    subscribeToDeviceTopics()
                    delay(Constants.DEVICE_CHECK_INTERVAL)
                }
            }

            1
        } catch (e: MqttException) {
            Log.e("MQTT", "Connection error: ${e.message}")
            -1
        }
    }

    private suspend fun subscribeToDeviceTopics() {
        val devices = appDatabase.deviceDao().getDevicesByBroker(broker.id)
        devices.forEach { device ->
            val deviceTopic = "zigbee/${device.ieeeAddr}"

            if (!subscribedTopics.contains(deviceTopic)) {
                mqttClient?.subscribe(deviceTopic, 0)
                subscribedTopics.add(deviceTopic)
                Log.i("MQTT", "Subscribed to topic: $deviceTopic")
            } else {
                Log.i("MQTT", "Already subscribed to topic: $deviceTopic")
            }
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
}