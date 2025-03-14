package com.example.iot_ha.ui.screens.home.devices

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry

@Composable
fun DeviceDetailScreen(backStackEntry: NavBackStackEntry) {
    val deviceId = backStackEntry.arguments?.getString("deviceId")?.toIntOrNull() ?: -1
    val context = LocalContext.current

    Text("Device ID: $deviceId")
}
