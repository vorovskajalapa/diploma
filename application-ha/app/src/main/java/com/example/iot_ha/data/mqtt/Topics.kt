package com.example.iot_ha.data.mqtt

object Topics {
    const val DISCOVERY_TOPIC = "zigbee/bridge/config/permit_join"

    const val DEVICE_STATE_TOPIC = "zigbee/0x"
    const val DEVICE_COMMANDS_TOPIC = "homeassistant/"
    const val DEVICE_LIST_TOPIC = "devicelist"
}