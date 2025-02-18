package com.example.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
fun DeviceScreen(db: AppDatabase) {
    val viewModel = remember { DeviceViewModel(db) }
    val devices = remember { mutableStateListOf<Device>() }

    LaunchedEffect(Unit) {
        viewModel.getDevices { devices.addAll(it) }
    }

    Column {
        Button(onClick = { viewModel.addDevice("New Device") }) {
            Text("Добавить устройство")
        }
        devices.forEach {
            Text(text = "${it.name} - ${if (it.status) "Включено" else "Выключено"}")
        }
    }
}
