package com.example.iot_ha.data.local.led

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object LEDState {
    private val _brightness = MutableStateFlow(127f)
    private val _red = MutableStateFlow(127f)
    private val _green = MutableStateFlow(127f)
    private val _blue = MutableStateFlow(127f)

    val brightness = _brightness.asStateFlow()
    val red = _red.asStateFlow()
    val green = _green.asStateFlow()
    val blue = _blue.asStateFlow()

    fun setBrightness(value: Float) { _brightness.value = value }
    fun setRed(value: Float) { _red.value = value }
    fun setGreen(value: Float) { _green.value = value }
    fun setBlue(value: Float) { _blue.value = value }
}
