package com.example.iot.ui.screens.home.device

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry

@Composable
fun DeviceDetailScreen(backStackEntry: NavBackStackEntry) {
    val deviceId = backStackEntry.arguments?.getString("deviceId") ?: "Unknown"
    Text(text = "Детали устройства с ID: $deviceId")
}
