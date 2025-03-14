package com.example.iot_ha.ui.viewmodels.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.device.Device
import kotlinx.coroutines.launch

class DevicesViewModel(private val db: RoomLocalDatabase) : ViewModel() {
    fun addDeviceIfNotExists(device: Device) {
        viewModelScope.launch {
            val existingDevice = db.deviceDAO().getDeviceByIeeeAddr(device.ieeeAddr)

            if (existingDevice == null) {
                db.deviceDAO().insertDevice(device)
                Log.i("DevicesViewModel", "Device added: $device")
            } else {
                Log.i("DevicesViewModel", "Device already exists: $device")
            }
        }
    }
}
