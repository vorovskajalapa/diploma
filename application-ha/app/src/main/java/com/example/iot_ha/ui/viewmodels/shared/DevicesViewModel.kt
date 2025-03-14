package com.example.iot_ha.ui.viewmodels.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.local.command.Command
import com.example.iot_ha.data.local.device.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DevicesViewModel(private val db: RoomLocalDatabase) : ViewModel() {
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices

    init {
        viewModelScope.launch {
            BrokerState.brokerId.collectLatest { brokerId ->
                if (brokerId != null) {
                    loadDevices(brokerId)
                }
            }
        }
    }

    private suspend fun loadDevices(brokerId: Int) {
        db.deviceDAO().getDevicesByBrokerFlow(brokerId)
            .collect { deviceList ->
                _devices.value = deviceList
            }
    }

    fun getDeviceIdByIeeeAddr(ieeeAddr: String, callback: (Int?) -> Unit) {
        viewModelScope.launch {
            val device = db.deviceDAO().getDeviceByIeeeAddr(ieeeAddr)
            callback(device?.id)
        }
    }


    fun addDeviceIfNotExists(device: Device) {
        viewModelScope.launch {
            val existingDevice = db.deviceDAO().getDeviceByIeeeAddr(device.ieeeAddr)

            Log.i("DEVLIST", devices.value.toString())

            if (existingDevice == null) {
                db.deviceDAO().insertDevice(device)
                Log.i("DevicesViewModel", "Device added: $device")
            } else {
                Log.i("DevicesViewModel", "Device already exists: $device")
            }
        }
    }

    fun addCommandIfNotExists(command: Command) {
        Log.i("addCommandIfNotExists", command.toString())

        viewModelScope.launch {
            val existingCommand = db.commandDAO().getCommandByCommandTopic(command.commandTopic)

            if (existingCommand == null) {
                db.commandDAO().insertCommand(command)
            } else {
                Log.i("DevicesViewModel", "Command already exists: $existingCommand")
            }
        }
    }


    fun changeSwitchDeviceState(deviceId: Int, checked: Boolean) {

    }
}

