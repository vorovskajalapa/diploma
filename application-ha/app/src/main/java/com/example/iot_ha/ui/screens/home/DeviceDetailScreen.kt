package com.example.iot_ha.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.iot_ha.data.local.device.DeviceState
import com.example.iot_ha.ui.components.devices.DeviceDetails
import com.example.iot_ha.ui.components.devices.DeviceNotFoundMessage
import com.example.iot_ha.ui.components.devices.DeviceTitle
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel

@Composable
fun DeviceDetailScreen(backStackEntry: NavBackStackEntry, devicesViewModel: DevicesViewModel) {
    val deviceId = backStackEntry.arguments?.getString("deviceId")?.toIntOrNull() ?: -1

    val devices by devicesViewModel.devices.collectAsState()

    val deviceState by DeviceState.devicesData.collectAsState()
    val deviceData = deviceState[deviceId]

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DeviceTitle(friendlyName = devices.find { it.id == deviceId }?.friendlyName ?: "Unknown Device")

            Spacer(modifier = Modifier.height(8.dp))

            if (deviceData != null) {
                DeviceDetails(deviceData)
            } else {
                DeviceNotFoundMessage()
            }
        }
    }
}


