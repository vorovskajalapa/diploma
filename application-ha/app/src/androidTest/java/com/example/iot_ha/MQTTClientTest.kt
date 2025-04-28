package com.example.iot_ha

import android.util.Log
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.mqtt.MQTTClient
import com.example.iot_ha.data.mqtt.domain.MQTTMessageHandler
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch

class MQTTClientTest {

    private lateinit var mqttClient: MQTTClient
    private lateinit var broker: Broker
    private lateinit var messageHandler: MQTTMessageHandler

    @Before
    fun setUp() {
        broker = Broker(
            id = 1,
            serverUri = "yahor.monster",
            serverPort = 1883,
            user = null,
            password = null
        )

        mqttClient = MQTTClient.initialize(broker, null)
    }

    @Test
    fun testConnect() {
        val latch = CountDownLatch(1)
        val success = mqttClient.connect()
        assertTrue(success)
        assertNotNull(mqttClient)
        mqttClient.publish("test/topic", "Hello MQTT!", 1, false)
        mqttClient.disconnect()
    }

    @Test
    fun testSubscribeAndReceiveMessage() {
        val latch = CountDownLatch(1)
        mqttClient.connect()
        mqttClient.subscribe("test/topic")
        mqttClient.publish("test/topic", "Test message", 1, false)
        mqttClient.disconnect()
    }
}
