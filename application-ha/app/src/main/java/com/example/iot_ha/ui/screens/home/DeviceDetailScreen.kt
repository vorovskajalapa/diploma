package com.example.iot_ha.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var selectedRoom by remember { mutableStateOf(currentRoom?.name ?: "Select room") }
    var expanded by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(device?.friendlyName ?: "") }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit device name") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        devicesViewModel.updateDeviceName(deviceId, newName)
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

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
                friendlyName = device?.friendlyName?.uppercase() ?: "Unknown Device",
                onEditClick = { showEditDialog = true }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Select room") },
                        onClick = {
                            selectedRoom = "Select room"
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
