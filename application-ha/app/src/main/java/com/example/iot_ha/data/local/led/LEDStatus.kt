package com.example.iot_ha.data.local.led

import org.json.JSONObject

data class LEDStatus(
    val state: String = "ON",
    val brightness: Int,
    val red: Int,
    val green: Int,
    val blue: Int,
    val colorMode: String = "rgb",
    val mode: String = "manual"
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("state", state)
            put("brightness", brightness)
            put("color", JSONObject().apply {
                put("r", red)
                put("g", green)
                put("b", blue)
            })
            put("color_mode", colorMode)
            put("mode", mode)
        }.toString()
    }
}
