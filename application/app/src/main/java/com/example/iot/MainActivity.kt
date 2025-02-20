package com.example.iot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.iot.data.local.AppDatabase
import com.example.iot.ui.screens.BrokerScreen
import com.example.iot.ui.viewmodel.BrokerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: BrokerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        viewModel = BrokerViewModel(db)

        setContent {
            BrokerScreen(viewModel)
        }
    }
}
