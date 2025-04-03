package com.example.iot_ha.ui.viewmodels.shared

import androidx.lifecycle.ViewModel
import com.example.iot_ha.ui.viewmodels.shared.data.SensorsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorsViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow<Map<String, SensorsData>>(emptyMap())
    val sensorData: StateFlow<Map<String, SensorsData>> = _sensorData

    fun updateSensorData(topic: String, payload: String) {
        _sensorData.value += (topic to SensorsData(topic, payload))
    }
}