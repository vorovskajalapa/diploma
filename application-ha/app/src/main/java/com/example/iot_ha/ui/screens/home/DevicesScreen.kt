package com.example.iot_ha.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.iot_ha.R
import com.example.iot_ha.data.local.device.DeviceState
import com.example.iot_ha.ui.components.devices.DeviceCard
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.utils.Constants
import com.example.iot_ha.utils.toBooleanState

@Composable
fun DevicesScreen(navHostController: NavHostController, devicesViewModel: DevicesViewModel) {
    val switchDevices by devicesViewModel.getDevicesByTypeFlow("switch").collectAsState()
    val devicesWithoutCommands by devicesViewModel.getDevicesWithoutCommandsFlow().collectAsState()
    val deviceState by DeviceState.devicesData.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (devicesWithoutCommands.isEmpty() && switchDevices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        text = "No connected devices",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                devicesWithoutCommands.forEach { device ->
                    DeviceCard(
                        deviceId = device.id,
                        imageRes = R.drawable.mqtt_logo,
                        name = device.friendlyName,
                        navController = navHostController,
                        onToggle = { state ->
                            devicesViewModel.onToggle(device.id, state)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                switchDevices.forEach { device ->
                    val deviceData = deviceState[device.id]

                    DeviceCard(
                        deviceId = device.id,
                        imageRes = R.drawable.mqtt_logo,
                        name = device.friendlyName,
                        type = Constants.SWITCH_TYPE,
                        value = (deviceData?.get("state") as? String)?.toBooleanState() ?: false,
                        navController = navHostController,
                        onToggle = { state ->
                            devicesViewModel.onToggle(device.id, state)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
