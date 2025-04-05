package com.example.iot_ha.ui.components.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.iot_ha.ui.components.common.CustomOutlinedTextField

@Composable
fun AddRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (roomName.isNotBlank()) {
                    onConfirm(roomName)
                    onDismiss()
                }
            }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        title = { Text("Добавить комнату") },
        text = {
            Column {
                Text("Введите название комнаты:")
                CustomOutlinedTextField(
                    value = roomName,
                    label = "Название комнаты",
                    onValueChange = { roomName = it }
                )
            }
        }
    )
}
