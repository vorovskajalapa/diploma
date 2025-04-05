package com.example.iot_ha.data.mqtt.interfaces

interface MQTTMessaging {
    fun subscribe(topic: String)
    fun unsubscribe(topic: String)
    fun publish(topic: String, payload: String, qos: Int = 1, retained: Boolean = false)
}
