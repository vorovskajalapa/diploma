package com.example.iot_ha.ui.viewmodels.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.local.command.Command
import com.example.iot_ha.data.local.device.Device
import com.example.iot_ha.data.mqtt.MQTTClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
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

            while (true) {
                updateDevices()
                delay(3000)
            }
        }
    }


    private suspend fun loadDevices(brokerId: Int) {
        db.deviceDAO().getDevicesByBrokerFlow(brokerId)
            .collect { deviceList ->
                _devices.value = deviceList
            }
    }

    private suspend fun updateDevices() {
        val devicesList = db.deviceDAO().getAllDevices()
        _devices.value = devicesList
    }


    fun getDeviceIdByIeeeAddr(ieeeAddr: String, callback: (Int?) -> Unit) {
        viewModelScope.launch {
            val device = db.deviceDAO().getDeviceByIeeeAddr(ieeeAddr)
            callback(device?.id)
        }
    }


    fun getDevicesByTypeFlow(type: String): StateFlow<List<Device>> {
        val resultFlow = MutableStateFlow<List<Device>>(emptyList())

        viewModelScope.launch {
            db.commandDAO().getCommandsByTypeFlow(type).collect { commands ->
                val deviceIds = commands.map { it.deviceId }.toSet()
                val filteredDevices = _devices.value.filter { it.id in deviceIds }
                resultFlow.value = filteredDevices
            }
        }

        return resultFlow
    }

    fun getDevicesWithoutCommandsFlow(): StateFlow<List<Device>> {
        val resultFlow = MutableStateFlow<List<Device>>(emptyList())

        viewModelScope.launch {
            db.deviceDAO().getAllDevicesFlow().collect { devices ->
                db.commandDAO().getAllCommandsFlow().collect { commands ->
                    val deviceIdsWithCommands = commands.map { it.deviceId }.toSet()
                    val devicesWithoutCommands = devices.filter { it.id !in deviceIdsWithCommands }
                    resultFlow.value = devicesWithoutCommands
                }
            }
        }

        return resultFlow
    }


    fun getDevicesByRoomIdFlow(roomId: Int): StateFlow<List<Device>> {
        val resultFlow = MutableStateFlow<List<Device>>(emptyList())

        viewModelScope.launch {
            db.deviceDAO().getDevicesByRoomIdFlow(roomId).collect { devices ->
                val deviceIds = devices.map { it.id }.toSet()
                val filteredDevices = _devices.value.filter { it.id in deviceIds }
                resultFlow.value = filteredDevices
            }
        }

        return resultFlow
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

    fun updateDeviceName(deviceId: Int, newName: String) {
        viewModelScope.launch {
            val device = db.deviceDAO().getDeviceById(deviceId)

            val updatedDevice = device.copy(friendlyName = newName)

            db.deviceDAO().updateDevice(updatedDevice)
            _devices.update { list ->
                list.map {
                    if (it.id == deviceId) updatedDevice else it
                }
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

    fun onToggle(deviceId: Int, state: Boolean) {
        viewModelScope.launch {
            val cmd = db.commandDAO().getSwitchCommandByDeviceId(deviceId)

            val newState = if (!state) cmd?.payloadOff else cmd?.payloadOn
            cmd?.let {
                if (newState != null) {
                    sendCommandToMqtt(it.commandTopic, newState)
                }
            }
        }
    }

    fun onSelectChange(deviceId: Int, option: String) {
        viewModelScope.launch {
            sendCommandToMqtt("command", "")
        }
    }

    fun onValueChange(deviceId: Int, value: Int) {
        viewModelScope.launch {
            sendCommandToMqtt("command", "")
        }
    }

    private fun sendCommandToMqtt(topic: String, command: String) {
        val mqttClient = MQTTClient.getInstance()
        mqttClient.publish(topic, command)
        println("Отправка в MQTT: $command")
    }

    fun assignRoomToDevice(deviceId: Int, roomId: Long?) {
        viewModelScope.launch {
            val device = db.deviceDAO().getDeviceById(deviceId)
            val updatedDevice = device.copy(roomId = roomId)
            db.deviceDAO().updateDevice(updatedDevice)
        }
    }

}

