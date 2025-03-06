package com.example.iot.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.device.Device
import kotlinx.coroutines.launch

class DeviceViewModel(db: AppDatabase) : ViewModel() {
    private val deviceDao = db.deviceDao()
    private val brokerDao = db.brokerDao()

    private var _devices = mutableStateOf<List<Device>>(emptyList())
    val devices: State<List<Device>> = _devices

    private fun loadDevices() {
        viewModelScope.launch {
            val broker = brokerDao.getLastBroker()
            _devices.value = deviceDao.getDevicesByBroker(broker.id)
        }
    }

    init {
        loadDevices()
    }
}
