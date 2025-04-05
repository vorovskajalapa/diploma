package com.example.iot_ha.data.local.broker.discovery

import com.example.iot_ha.data.mqtt.MQTTClient
import com.example.iot_ha.data.mqtt.util.Topics
import com.example.iot_ha.utils.Constants
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
        MQTTClient.publish(Topics.DISCOVERY_TOPIC, Constants.DISCOVERY_ENABLE)

        resetJob?.cancel()

        resetJob = CoroutineScope(Dispatchers.Default).launch {
            delay(Constants.DISCOVERY_TIME)
            _isDiscoveryActive.value = false
        }
    }

    fun stopDiscovery() {
        _isDiscoveryActive.value = false
        MQTTClient.publish(Topics.DISCOVERY_TOPIC, Constants.DISCOVERY_DISABLE)
        resetJob?.cancel()
    }
}
