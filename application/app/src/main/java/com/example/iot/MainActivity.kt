package com.example.iot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.iot.data.local.AppDatabase
import com.example.iot.ui.screens.AuthorizationScreen
import com.example.iot.ui.viewmodel.AuthorizationViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val viewModel = AuthorizationViewModel(db)

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController, viewModel)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, viewModel: AuthorizationViewModel) {
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") { AuthorizationScreen(navController, viewModel) }
        composable("home") { HomeScreen() }
    }
}
