package com.example.iot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.mqtt.MqttClientHelper
import com.example.iot.ui.screens.AuthorizationScreen
import com.example.iot.ui.screens.HomeScreen
import com.example.iot.ui.viewmodel.AuthorizationViewModel
import com.example.iot.ui.viewmodel.factory.AuthorizationViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)

        lifecycleScope.launch {
            val lastBroker = db.brokerDao().getLastBroker()

            lastBroker.let {
                MqttClientHelper.initialize(applicationContext, it)
            }

            val authorizationViewModel: AuthorizationViewModel by viewModels {
                AuthorizationViewModelFactory(db)
            }

            val startDestination = authorizationViewModel.getStartDestination()

            setContent {
                val navController = rememberNavController()
                AppNavHost(navController, startDestination)
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") { AuthorizationScreen(navController) }
        composable("home") { HomeScreen() }
    }
}