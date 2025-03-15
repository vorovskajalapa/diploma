package com.example.iot_ha.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.ui.screens.AuthorizationScreen
import com.example.iot_ha.ui.screens.HomeScreen
import com.example.iot_ha.ui.screens.home.DeviceDetailScreen
import com.example.iot_ha.ui.screens.home.RoomDetailScreen
import com.example.iot_ha.ui.viewmodels.factory.DevicesViewModelFactory
import com.example.iot_ha.ui.viewmodels.factory.RoomsViewModelFactory
import com.example.iot_ha.ui.viewmodels.factory.SensorsViewModelFactory
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.ui.viewmodels.shared.RoomsViewModel
import com.example.iot_ha.ui.viewmodels.shared.SensorsViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    val db = RoomLocalDatabase.getInstance(LocalContext.current)

    val sensorsViewModel: SensorsViewModel = viewModel(factory = SensorsViewModelFactory())
    val devicesViewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(db))
    val roomsViewModel: RoomsViewModel = viewModel(factory = RoomsViewModelFactory(db))

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.AUTH_SCREEN) {
            AuthorizationScreen(
                navHostController = navController,
                sensorsViewModel = sensorsViewModel,
                devicesViewModel = devicesViewModel
            )
        }

        composable(Routes.HOME_SCREEN) {
            HomeScreen(
                navHostController = navController,
                devicesViewModel = devicesViewModel,
                roomsViewModel = roomsViewModel
            )
        }

        composable(Routes.DEVICE_DETAILS_SCREEN) { backStackEntry ->
            DeviceDetailScreen(
                backStackEntry = backStackEntry,
                devicesViewModel = devicesViewModel,
                roomsViewModel = roomsViewModel
            )
        }

        composable(Routes.ROOM_DETAILS_SCREEN) { backStackEntry ->
            RoomDetailScreen(
                backStackEntry = backStackEntry,
                navHostController = navController,
                devicesViewModel = devicesViewModel,
                roomsViewModel = roomsViewModel
            )
        }
    }
}

