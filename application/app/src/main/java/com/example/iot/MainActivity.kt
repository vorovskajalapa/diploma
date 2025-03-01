package com.example.iot

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.View
import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.iot.data.local.AppDatabase
import com.example.iot.ui.screens.AuthorizationScreen
import com.example.iot.ui.screens.HomeScreen
import com.example.iot.ui.viewmodel.AuthorizationViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.iot.data.mqtt.MqttClientHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : ComponentActivity() {
    private lateinit var mqttClientHelper: MqttClientHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val viewModel = AuthorizationViewModel(db)

        mqttClientHelper = MqttClientHelper(applicationContext)

        lifecycleScope.launch {
            // Проверяем, есть ли сохранённый брокер
            val startDestination = if (viewModel.hasSavedBrokerInDb()) "auth" else "auth"

            // Подключаемся к MQTT
            mqttClientHelper.connect()

            delay(2000) // Ждём 2 секунды на подключение
            sendTestMessage()

            runOnUiThread {
                setContent {
                    val navController = rememberNavController()
                    AppNavHost(navController, viewModel, startDestination)
                }
            }
        }
    }

    private fun sendTestMessage() {
        try {
            val message = MqttMessage("Hello from Android".toByteArray())
            message.qos = 1
            mqttClientHelper.mqttClient?.publish("test_publish", message)
            println("✅ MQTT: Сообщение отправлено в test_publish")
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ MQTT: Ошибка отправки сообщения: " + e.message)
        }
    }
}


@Composable
fun AppNavHost(navController: NavHostController, viewModel: AuthorizationViewModel, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") { AuthorizationScreen(navController, viewModel) }
        composable("home") { HomeScreen() }
    }
}
