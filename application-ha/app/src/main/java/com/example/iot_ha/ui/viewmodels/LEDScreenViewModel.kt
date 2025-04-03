package com.example.iot_ha.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.led.LEDState
import com.example.iot_ha.data.local.led.LEDStatus
import com.example.iot_ha.data.mqtt.MQTTClient
import com.example.iot_ha.data.mqtt.util.Topics
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LEDScreenViewModel : ViewModel() {

    // Отправить статус LED
    fun sendLEDStatus() {
        viewModelScope.launch {
            val brightness = LEDState.brightness.first().toInt()
            val red = LEDState.red.first().toInt()
            val green = LEDState.green.first().toInt()
            val blue = LEDState.blue.first().toInt()

            val ledStatus = LEDStatus(
                brightness = brightness,
                red = red,
                green = green,
                blue = blue
            )

            MQTTClient.publish(Topics.LED_SET_STATE_TOPIC, ledStatus.toJson())
        }
    }

    fun setAUTOMode() {
        viewModelScope.launch {
            // Статус AUTO
            val ledAutoStatus = """
                {
                  "state": "OFF",
                  "brightness": 255,
                  "color": {
                    "r": 255,
                    "g": 255,
                    "b": 255
                  },
                  "color_mode": "rgb",
                  "mode": "auto"
                }
            """.trimIndent()

            MQTTClient.publish(Topics.LED_SET_STATE_TOPIC, ledAutoStatus)
        }
    }
}
