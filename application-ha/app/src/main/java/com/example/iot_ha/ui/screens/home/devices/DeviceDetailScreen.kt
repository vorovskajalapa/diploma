package com.example.iot_ha.ui.screens.home.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import com.example.iot_ha.data.local.RoomLocalDatabase

@Composable
fun DeviceDetailScreen(backStackEntry: NavBackStackEntry) {
    val deviceId = backStackEntry.arguments?.getString("deviceId")?.toIntOrNull() ?: -1
    val context = LocalContext.current
}
