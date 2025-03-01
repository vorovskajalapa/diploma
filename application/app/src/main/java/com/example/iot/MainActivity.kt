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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val db = AppDatabase.getInstance(applicationContext)
        val viewModel = AuthorizationViewModel(db)

        lifecycleScope.launch {
            // first auth -> home
            val startDestination = if (viewModel.hasSavedBrokerInDb()) "auth" else "auth"
            // check if reachable connection and them home
            // else auth

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
