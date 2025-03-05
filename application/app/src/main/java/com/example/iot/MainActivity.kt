package com.example.iot

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity : ComponentActivity() {
    private lateinit var mqttClientHelper: MqttClientHelper

    private suspend fun connectWithTimeout(): Boolean {
        return withTimeoutOrNull(2000) {
            mqttClientHelper.connect() == 1
        } ?: false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val viewModel = AuthorizationViewModel(db)

        mqttClientHelper = MqttClientHelper(applicationContext)

        lifecycleScope.launch {
            val checkSavedInstance = viewModel.hasSavedBrokerInDb()
            var startDestination = "auth"

            if (checkSavedInstance) {
                if (connectWithTimeout()) {
                    startDestination = "home";
                }
            }

            runOnUiThread {
                setContent {
                    val navController = rememberNavController()
                    AppNavHost(navController, viewModel, startDestination)
                }
            }
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
