package com.example.iot.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.device.Device
import com.example.iot.data.mqtt.MqttClientHelper
import kotlinx.coroutines.launch

class DeviceViewModel(db: AppDatabase, private val mqttClientHelper: MqttClientHelper) :
    ViewModel() {
    private val deviceDao = db.deviceDao()
    private val brokerDao = db.brokerDao()
    private val deviceConfigDAO = db.deviceConfigDao()

    private var _devices = mutableStateOf<List<Device>>(emptyList())
    val devices: State<List<Device>> = _devices

    private fun loadDevices() {
        viewModelScope.launch {
            val broker = brokerDao.getLastBroker()
            if (broker != null) {
                deviceDao.getDevicesByBrokerFlow(broker.id)
                    .collect { deviceList ->
                        _devices.value = deviceList
                    }
            }
        }
    }

    // current state, topic
    // field from devices config
    /*
        1) get device (topic / ieeAddr)
        2) current state
        3) !(current state) send to topic \
        4)                                  get switch field from modelid and config
     */
    fun changeSwitchDeviceState(devId: Int, state: Boolean) {
        viewModelScope.launch {
            val device = deviceDao.getDeviceById(devId)
            val field = deviceConfigDAO.getSwitchFieldByModelId(device.modelId)?.field

            val formatTopic = device.topic + "/" + "set" + "/" + field
            val newState = if (state) "ON" else "OFF"
            mqttClientHelper.publishMessage(formatTopic, newState)
        }
    }

    init {
        loadDevices()
    }
}
