package com.example.iot.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.mqtt.MqttClientHelper
import com.example.iot.ui.viewmodel.AuthorizationViewModel

class AuthorizationViewModelFactory(private val db: AppDatabase, private val mqttClient: MqttClientHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorizationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthorizationViewModel(db, mqttClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
