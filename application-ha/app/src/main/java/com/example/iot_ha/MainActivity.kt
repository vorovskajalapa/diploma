package com.example.iot_ha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot_ha.ui.screens.AuthorizationScreen
import com.example.iot_ha.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {
//    private val broker = Broker(
//        serverUri = "m1.wqtt.ru",
//        serverPort = 13058,
//        user = "yahor1",
//        password = "yahor1"
//    )
//
//    private val mqttClient = MQTTClient.initialize(broker)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController, "auth")
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            mqttClient.connect()
//        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") { AuthorizationScreen(navHostController = navController) }
        composable("home") { HomeScreen(navHostController = navController) }
    }
}


