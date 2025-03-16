package com.example.iot_ha.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.iot_ha.ui.components.rooms.AddRoomDialog
import com.example.iot_ha.ui.components.rooms.RoomCard
import com.example.iot_ha.ui.viewmodels.shared.RoomsViewModel

@Composable
fun RoomsScreen(
    navHostController: NavHostController,
    roomsViewModel: RoomsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val rooms by roomsViewModel.rooms.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(rooms) { room ->
                    var deviceCount by remember { mutableIntStateOf(0) }

                    LaunchedEffect(room.id) {
                        roomsViewModel.getDeviceCount(room.id) { count ->
                            deviceCount = count
                        }
                    }

                    RoomCard(
                        roomId = room.id.toInt(),
                        roomName = room.name,
                        deviceCount = deviceCount,
                        navHostController = navHostController
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить комнату")
        }
    }

    if (showDialog) {
        AddRoomDialog(
            onDismiss = { showDialog = false },
            onConfirm = { roomName ->
                roomsViewModel.addRoom(roomName)
                showDialog = false
                Log.i("Rooms", "Комната добавлена: $roomName")
            }
        )
    }
}

