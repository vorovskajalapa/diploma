package com.example.iot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DeviceViewModel(private val db: AppDatabase) : ViewModel() {
    fun addDevice(name: String) {
        viewModelScope.launch {
            db.deviceDao().insertDevice(Device(name = name, status = false))
        }
    }

    fun getDevices(onResult: (List<Device>) -> Unit) {
        viewModelScope.launch {
            val devices = db.deviceDao().getAllDevices()
            onResult(devices)
        }
    }
}
