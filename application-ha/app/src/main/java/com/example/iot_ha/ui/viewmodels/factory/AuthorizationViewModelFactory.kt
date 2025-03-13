package com.example.iot_ha.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.ui.viewmodels.AuthorizationViewModel

class AuthorizationViewModelFactory(
    private val db: RoomLocalDatabase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorizationViewModel::class.java)) {
            return AuthorizationViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}