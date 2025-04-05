package com.example.iot_ha.ui.viewmodels.shared.data

data class SensorsData(
    val topic: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)
