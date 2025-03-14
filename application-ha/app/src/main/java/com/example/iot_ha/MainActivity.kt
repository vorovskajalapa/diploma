package com.example.iot_ha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.ui.screens.AuthorizationScreen
import com.example.iot_ha.ui.screens.HomeScreen
import com.example.iot_ha.ui.screens.home.devices.DeviceDetailScreen
import com.example.iot_ha.ui.viewmodels.factory.DevicesViewModelFactory
import com.example.iot_ha.ui.viewmodels.factory.SensorsViewModelFactory
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.ui.viewmodels.shared.SensorsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController, "auth")
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    val db = RoomLocalDatabase.getInstance(LocalContext.current)

    val sensorsViewModel: SensorsViewModel = viewModel(factory = SensorsViewModelFactory())
    val devicesViewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(db))
    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthorizationScreen(
                navHostController = navController,
                sensorsViewModel = sensorsViewModel,
                devicesViewModel = devicesViewModel
            )
        }
        composable("home") {
            HomeScreen(
                navHostController = navController,
                devicesViewModel = devicesViewModel
            )
        }

        composable("device_details/{deviceId}") { backStackEntry ->
            DeviceDetailScreen(backStackEntry)
        }
    }
}


