package com.example.iot.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iot.data.local.AppDatabase
import com.example.iot.ui.viewmodel.AuthorizationViewModel

class AuthorizationViewModelFactory(
    private val db: AppDatabase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorizationViewModel::class.java)) {
            return AuthorizationViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}