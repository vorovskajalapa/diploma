package com.example.iot.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.mqtt.MqttClientHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AuthorizationViewModel(
    private val db: AppDatabase,
    private val mqttClient: MqttClientHelper
) : ViewModel() {

    private val brokerDao = db.brokerDao()

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

    private suspend fun connectWithTimeout(): Boolean {
        return withTimeoutOrNull(2000) {
            mqttClient.connect() == 1
        } ?: false
    }

    private suspend fun hasSavedBrokerInDb(): Boolean {
        return brokerDao.getAllBrokers().isNotEmpty()
    }

    suspend fun getStartDestination(): String {
        val hasSavedBroker = hasSavedBrokerInDb()
        return if (hasSavedBroker && connectWithTimeout()) "home" else "auth"
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
            brokerDao.deleteBroker(broker)
            loadBrokers()
        }
    }
}
