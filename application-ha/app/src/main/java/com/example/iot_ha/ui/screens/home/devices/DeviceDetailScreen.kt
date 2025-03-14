package com.example.iot_ha.ui.screens.home.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import com.example.iot_ha.data.local.device.DeviceState
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel

@Composable
fun DeviceDetailScreen(backStackEntry: NavBackStackEntry, devicesViewModel: DevicesViewModel) {
    val deviceId = backStackEntry.arguments?.getString("deviceId")?.toIntOrNull() ?: -1
    val devices by devicesViewModel.devices.collectAsState()
    val friendlyName = devices.find { it.id == deviceId }?.friendlyName ?: "Unknown Device"
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
            Text(
                text = friendlyName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (deviceData != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    deviceData.forEach { (key, value) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "$key: $value",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Device not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
