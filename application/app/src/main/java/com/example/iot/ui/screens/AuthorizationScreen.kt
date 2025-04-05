package com.example.iot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.iot.R
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.broker.Broker
import com.example.iot.data.mqtt.MqttClientHelper
import com.example.iot.ui.viewmodel.AuthorizationViewModel
import com.example.iot.ui.viewmodel.factory.AuthorizationViewModelFactory

@Composable
fun AuthorizationScreen(navController: NavHostController) {
    var serverUri by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    val db = AppDatabase.getInstance(LocalContext.current)
    val authorizationViewModel: AuthorizationViewModel =
        viewModel(factory = (AuthorizationViewModelFactory(db)))

    val brokers = authorizationViewModel.brokers.value


    val lastBroker = brokers.lastOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.mqtt_logo),
            contentDescription = "Broker Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .aspectRatio(4f)
                .clip(RoundedCornerShape(12.dp)),
        )

        Spacer(modifier = Modifier.height(50.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = serverUri,
                onValueChange = { serverUri = it },
                label = { Text("Server URI") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = serverPort,
                onValueChange = { serverPort = it },
                label = { Text("Server Port") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("User (optional)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (optional)") },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (serverUri.isNotBlank() && serverPort.isNotBlank()) {
                        authorizationViewModel.addBroker(
                            serverUri,
                            serverPort.toIntOrNull() ?: 1883,
                            user.takeIf { it.isNotBlank() },
                            password.takeIf { it.isNotBlank() }
                        )
                        serverUri = ""
                        serverPort = ""
                        user = ""
                        password = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Добавить брокера", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        lastBroker?.let { broker ->
            Text("Последний сохранённый брокер:", style = MaterialTheme.typography.labelMedium)

            Spacer(modifier = Modifier.height(1.dp))

            BrokerItem(
                broker,
                onDelete = { authorizationViewModel.deleteBroker(broker) },
                onLogin = {
                    val isSuccess = MqttClientHelper.reinitialize(null, broker)
                    if (isSuccess) {
                        navController.navigate("home")
                    }
                }
            )
        }
    }
}

@Composable
fun BrokerItem(broker: Broker, onDelete: () -> Unit, onLogin: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "URI: ${broker.serverUri}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Port: ${broker.serverPort}", style = MaterialTheme.typography.bodyMedium)
            broker.user?.let {
                Text(
                    text = "User: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            broker.password?.let {
                Text(
                    text = "Password: ${"*".repeat(it.length)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onLogin,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Войти", color = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить")
                }
            }
        }
    }
}
