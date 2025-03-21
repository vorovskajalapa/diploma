package com.example.iot_ha.data.mqtt.util

object Topics {
    const val DISCOVERY_TOPIC = "zigbee/bridge/config/permit_join"

    const val DEVICE_STATE_TOPIC = "zigbee/0x"
    const val SUBSCRIBE_DEVICE_STATE_TOPIC = "zigbee/#"

    const val DEVICE_COMMANDS_TOPIC = "homeassistant/"
    const val SUBSCRIBE_DEVICE_COMMANDS_TOPIC = "homeassistant/#"

    const val DEVICE_LIST_TOPIC = "devicelist"
    const val SUBSCRIBE_DEVICE_LIST_TOPIC = "devicelist"

    const val LED_STATE_TOPIC = "zigbee/led"
    const val SUBSCRIBE_LED_STATE_TOPIC = "zigbee/led"
    const val LED_SET_STATE_TOPIC = "zigbee/led/set"
}