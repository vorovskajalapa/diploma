package com.example.iot_ha.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.iot_ha.ui.components.rooms.AddRoomDialog
import com.example.iot_ha.ui.components.rooms.RoomCard

data class RoomModel(
    val id: Int,
    val name: String
)

@Composable
fun RoomsScreen(navHostController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }

    var rooms by remember {
        mutableStateOf(
            listOf(
                RoomModel(1, "Гостиная"),
                RoomModel(2, "Кухня"),
                RoomModel(3, "Спальня"),
                RoomModel(4, "Кабинет")
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(rooms) { room ->
                    RoomCard(roomId = room.id, roomName = room.name, navHostController = navHostController)
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
                val newRoom = RoomModel(
                    id = (rooms.maxOfOrNull { it.id } ?: 0) + 1,
                    name = roomName
                )
                rooms = rooms + newRoom
                showDialog = false
                Log.i("Rooms", "Комната добавлена: ${newRoom.name} (ID: ${newRoom.id})")
            }
        )
    }
}
