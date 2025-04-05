package com.example.iot_ha.data.mqtt.interfaces

interface MQTTConnection {
    fun connect(): Boolean
    fun disconnect()
}
