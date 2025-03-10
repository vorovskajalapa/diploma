package com.example.iot.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.iot.R
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.mqtt.MqttClientHelper
import com.example.iot.ui.components.DeviceCard
import com.example.iot.ui.viewmodel.DeviceViewModel
import com.example.iot.ui.viewmodel.factory.DeviceViewModelFactory

@Composable
fun DevicesScreen(navHostController: NavHostController) {

    val db = AppDatabase.getInstance(LocalContext.current)
    val mqttClientHelper = MqttClientHelper.getInstance()
    val deviceViewModel: DeviceViewModel =
        viewModel(factory = DeviceViewModelFactory(db, mqttClientHelper))



    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            deviceViewModel.devices.value.forEach { device ->
                DeviceCard(
                    deviceId = device.id,
                    imageRes = R.drawable.mqtt_logo,
                    name = device.friendlyName,
                    type = "switch",
                    value = false,
                    navController = navHostController,
                    onToggle = { deviceId, isChecked ->
                        deviceViewModel.changeSwitchDeviceState(deviceId, isChecked)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}