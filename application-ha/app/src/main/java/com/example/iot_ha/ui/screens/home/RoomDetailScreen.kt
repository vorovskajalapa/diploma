package com.example.iot_ha.ui.screens.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.example.iot_ha.ui.viewmodels.shared.RoomsViewModel

@Composable
fun RoomDetailScreen(backStackEntry: NavBackStackEntry) {
    val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: -1

    Text("Room $roomId")
}
