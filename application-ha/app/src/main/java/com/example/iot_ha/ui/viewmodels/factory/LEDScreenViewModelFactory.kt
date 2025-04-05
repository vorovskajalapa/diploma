package com.example.iot_ha.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot_ha.ui.viewmodels.LEDScreenViewModel

class LEDScreenViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LEDScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LEDScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}