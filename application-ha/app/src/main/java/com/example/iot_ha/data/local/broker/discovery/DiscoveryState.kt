package com.example.iot_ha.data.local.broker.discovery

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object DiscoveryState {
    private val _isDiscoveryActive = MutableStateFlow(false)
    val isDiscoveryActive: StateFlow<Boolean> = _isDiscoveryActive

    private var resetJob: Job? = null

    fun startDiscovery() {
        _isDiscoveryActive.value = true

        resetJob?.cancel()

        resetJob = CoroutineScope(Dispatchers.Default).launch {
            delay(255_000)
            _isDiscoveryActive.value = false
        }
    }

    fun stopDiscovery() {
        _isDiscoveryActive.value = false
        resetJob?.cancel()
    }
}
