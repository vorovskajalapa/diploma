package com.example.iot_ha.ui.components.broker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.iot_ha.ui.components.common.CustomOutlinedTextField

@Composable
fun BrokerInputForm(
    serverUri: String,
    serverPort: String,
    user: String,
    password: String,
    onServerUriChange: (String) -> Unit,
    onServerPortChange: (String) -> Unit,
    onUserChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAddBroker: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CustomOutlinedTextField(
            value = serverUri,
            label = "Server URI",
            onValueChange = onServerUriChange
        )
        CustomOutlinedTextField(
            value = serverPort,
            label = "Server Port",
            onValueChange = onServerPortChange
        )
        CustomOutlinedTextField(
            value = user,
            label = "User (optional)",
            onValueChange = onUserChange
        )
        CustomOutlinedTextField(
            value = password,
            label = "Password (optional)",
            onValueChange = onPasswordChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAddBroker,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add broker", color = Color.White)
        }
    }
}
