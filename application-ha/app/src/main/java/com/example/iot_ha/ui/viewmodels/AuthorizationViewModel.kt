package com.example.iot_ha.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.Broker
import kotlinx.coroutines.launch

class AuthorizationViewModel(
    private val db: RoomLocalDatabase,
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
            brokerDao.deleteBroker(broker)
            loadBrokers()
        }
    }
}