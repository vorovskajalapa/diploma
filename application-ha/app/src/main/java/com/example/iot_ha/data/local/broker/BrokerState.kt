package com.example.iot_ha.data.local.broker

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object BrokerState {
    private val _brokerId = MutableStateFlow<Int?>(null)
    val brokerId: StateFlow<Int?> = _brokerId

    fun setBrokerId(id: Int) {
        _brokerId.value = id
    }
}
