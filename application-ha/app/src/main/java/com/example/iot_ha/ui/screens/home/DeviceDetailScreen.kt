package com.example.iot_ha.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.iot_ha.data.local.device.DeviceState
import com.example.iot_ha.ui.components.devices.DeviceDetails
import com.example.iot_ha.ui.components.devices.DeviceNotFoundMessage
import com.example.iot_ha.ui.components.devices.DeviceTitle
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.ui.viewmodels.shared.RoomsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    backStackEntry: NavBackStackEntry,
    devicesViewModel: DevicesViewModel,
    roomsViewModel: RoomsViewModel
) {
    val deviceId = backStackEntry.arguments?.getString("deviceId")?.toIntOrNull() ?: -1

    val devices by devicesViewModel.devices.collectAsState()
    val deviceState by DeviceState.devicesData.collectAsState()
    val deviceData = deviceState[deviceId]

    val rooms by roomsViewModel.rooms.collectAsState()
    val device = devices.find { it.id == deviceId }
    val currentRoom = rooms.find { it.id == device?.roomId }
    var selectedRoom by remember { mutableStateOf(currentRoom?.name ?: "Выберите комнату") }
    var expanded by remember { mutableStateOf(false) }

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
            DeviceTitle(
                friendlyName = device?.friendlyName?.uppercase() ?: "Unknown Device"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedRoom,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Выберите комнату") },
                        onClick = {
                            selectedRoom = "Выберите комнату"
                            expanded = false
                            devicesViewModel.assignRoomToDevice(deviceId, null)
                        }
                    )
                    rooms.forEach { room ->
                        DropdownMenuItem(
                            text = { Text(room.name) },
                            onClick = {
                                selectedRoom = room.name
                                expanded = false
                                devicesViewModel.assignRoomToDevice(deviceId, room.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (deviceData != null) {
                DeviceDetails(deviceData)
            } else {
                DeviceNotFoundMessage()
            }
        }
    }
}
