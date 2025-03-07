package com.example.iot.ui.screens.home

import android.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.mqtt.MqttClientHelper
import com.example.iot.ui.components.DeviceCard
import com.example.iot.ui.viewmodel.DeviceViewModel
import com.example.iot.ui.viewmodel.factory.DeviceViewModelFactory

@Composable
fun DevicesScreen(mqttClientHelper: MqttClientHelper) {

    val db = AppDatabase.getInstance(LocalContext.current)
    val viewModel: DeviceViewModel =
        viewModel(factory = DeviceViewModelFactory(db, mqttClientHelper))


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            DeviceCard(
                deviceId = 2,
                imageRes = R.drawable.ic_menu_gallery,
                name = "Smart Light",
                type = "switch",
                value = true,
                onToggle = { deviceId, isChecked ->
                    viewModel.changeSwitchDeviceState(deviceId, isChecked)
                }
            )
        }
    }
}