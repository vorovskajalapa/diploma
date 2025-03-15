package com.example.iot_ha.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.ui.viewmodels.shared.RoomsViewModel

class RoomsViewModelFactory(private val db: RoomLocalDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomsViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}