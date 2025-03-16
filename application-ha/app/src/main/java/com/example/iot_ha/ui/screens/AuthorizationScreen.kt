package com.example.iot_ha.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.iot_ha.R
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.local.broker.BrokerState
import com.example.iot_ha.data.mqtt.MQTTClient
import com.example.iot_ha.data.mqtt.MQTTMessageHandler
import com.example.iot_ha.ui.components.broker.BrokerInputForm
import com.example.iot_ha.ui.components.broker.BrokerList
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
                delay(500)
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

        BrokerInputForm(
            serverUri = serverUri,
            serverPort = serverPort,
            user = user,
            password = password,
            onServerUriChange = { serverUri = it },
            onServerPortChange = { serverPort = it },
            onUserChange = { user = it },
            onPasswordChange = { password = it },
            onAddBroker = {
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
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        BrokerList(
            brokers = brokers,
            onDelete = { handleDelete(it, authorizationViewModel) },
            onLogin = { handleLogin(it, messageHandler, navHostController) }
        )
    }
}