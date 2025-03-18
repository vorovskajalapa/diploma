package com.example.iot_ha.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.mqtt.MQTTClient
import com.example.iot_ha.data.mqtt.domain.MQTTMessageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthorizationViewModel(
    private val db: RoomLocalDatabase,
    private val messageHandler: MQTTMessageHandler
) : ViewModel() {

    private val brokerDao = db.brokerDAO()

    private var _brokers = mutableStateOf<List<Broker>>(emptyList())
    val brokers: State<List<Broker>> = _brokers

    init {
        loadBrokers()
    }

    private fun loadBrokers() {
        viewModelScope.launch {
            _brokers.value = brokerDao.getAllBrokers()
        }
    }

    fun addBroker(serverUri: String, serverPort: Int, user: String?, password: String?) {
        viewModelScope.launch {
            val broker = Broker(
                serverUri = serverUri,
                serverPort = serverPort,
                user = user,
                password = password
            )
            brokerDao.insert(broker)
            loadBrokers()
        }
    }

    fun deleteBroker(broker: Broker) {
        viewModelScope.launch {
            MQTTClient.getInstance().disconnect()
            brokerDao.deleteBroker(broker)
            loadBrokers()
        }
    }

    fun handleLogin(broker: Broker, onSuccess: () -> Unit) {
        BrokerState.setBrokerId(broker.id)

        val mqttClient = MQTTClient.reinitialize(broker, messageHandler)
        val isSuccess = mqttClient.connect()
        if (isSuccess) {
            onSuccess()

            viewModelScope.launch(Dispatchers.IO) {
                mqttClient.subscribe("devicelist")
                delay(500)
                mqttClient.subscribe("homeassistant/#")
                mqttClient.subscribe("zigbee/#")
            }
        }
    }
}
