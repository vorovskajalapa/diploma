package com.example.iot_ha.ui.screens

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
import com.example.iot_ha.R
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.mqtt.MQTTClient
import com.example.iot_ha.data.mqtt.MQTTMessageHandler
import com.example.iot_ha.ui.viewmodels.AuthorizationViewModel
import com.example.iot_ha.ui.viewmodels.factory.AuthorizationViewModelFactory
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.ui.viewmodels.shared.SensorsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthorizationScreen(
    navHostController: NavHostController,
    sensorsViewModel: SensorsViewModel,
    devicesViewModel: DevicesViewModel
) {
    var serverUri by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val db = RoomLocalDatabase.getInstance(LocalContext.current)
    val authorizationViewModel: AuthorizationViewModel =
        viewModel(factory = (AuthorizationViewModelFactory(db)))

    val brokers = authorizationViewModel.brokers.value

    val messageHandler = remember { MQTTMessageHandler(sensorsViewModel, devicesViewModel) }


    val lastBroker = brokers.lastOrNull()

    fun handleLogin(
        broker: Broker,
        messageHandler: MQTTMessageHandler,
        navHostController: NavHostController
    ) {
        BrokerState.setBrokerId(broker.id)

        val mqttClient = MQTTClient.reinitialize(broker, messageHandler)
        val isSuccess = mqttClient.connect()
        if (isSuccess) {
            navHostController.navigate("home")

            CoroutineScope(Dispatchers.IO).launch {
                mqttClient.subscribe("devicelist")
                delay(500) // todo: fix (если получится), хз не фикситься, не успевает обработать
                mqttClient.subscribe("homeassistant/#")
                mqttClient.subscribe("zigbee/#")
            }
        }
    }

    fun handleDelete(broker: Broker, authorizationViewModel: AuthorizationViewModel) {
        val mqttClient = MQTTClient.getInstance()
        mqttClient.disconnect()
        authorizationViewModel.deleteBroker(broker)
    }

    fun handleAddBroker(
        serverUri: String,
        serverPort: String,
        user: String,
        password: String,
        onClearFields: () -> Unit,
        authorizationViewModel: AuthorizationViewModel
    ) {
        if (serverUri.isNotBlank() && serverPort.isNotBlank()) {
            authorizationViewModel.addBroker(
                serverUri,
                serverPort.toIntOrNull() ?: 1883,
                user.takeIf { it.isNotBlank() },
                password.takeIf { it.isNotBlank() }
            )
            onClearFields()
        }
    }



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
                    handleAddBroker(
                        serverUri,
                        serverPort,
                        user,
                        password,
                        onClearFields = {
                            serverUri = ""
                            serverPort = ""
                            user = ""
                            password = ""
                        },
                        authorizationViewModel
                    )
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
                onDelete = { handleDelete(broker, authorizationViewModel) },
                onLogin = { handleLogin(broker, messageHandler, navHostController) }
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
