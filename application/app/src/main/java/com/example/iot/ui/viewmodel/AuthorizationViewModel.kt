package com.example.iot.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.broker.Broker
import kotlinx.coroutines.launch

class AuthorizationViewModel(private val db: AppDatabase) : ViewModel() {
    private val brokerDao = db.brokerDao()

    private val _brokers = MutableLiveData<List<Broker>>(emptyList())
    val brokers: LiveData<List<Broker>> = _brokers

    init {
        loadBrokers()
    }

    private fun loadBrokers() {
        viewModelScope.launch {
            _brokers.postValue(brokerDao.getAllBrokers())
        }
    }

    suspend fun hasSavedBrokerInDb(): Boolean {
        return brokerDao.getAllBrokers().isNotEmpty()
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
