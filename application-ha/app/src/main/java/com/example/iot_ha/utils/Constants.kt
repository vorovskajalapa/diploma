package com.example.iot_ha.utils

object Constants {
    const val SWITCH_TYPE = "switch"
    const val SELECT_TYPE = "select"
    const val DIMMER_TYPE = "dimmer"

    val TABS_LIST = listOf("Devices", "Rooms", "Settings")

    const val DISCOVERY_ENABLE = "true"
    const val DISCOVERY_DISABLE = "false"
    const val DISCOVERY_TIME = 255_000L
}