package com.example.iot_ha.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel

class DevicesViewModelFactory(private val db: RoomLocalDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevicesViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}