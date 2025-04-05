package com.example.iot_ha.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot_ha.ui.viewmodels.shared.SensorsViewModel

class SensorsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorsViewModel::class.java)) {
            return SensorsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
