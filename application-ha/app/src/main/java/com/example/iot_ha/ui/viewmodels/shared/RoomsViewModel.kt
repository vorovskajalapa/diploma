package com.example.iot_ha.ui.viewmodels.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.room.RoomEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoomsViewModel(private val db: RoomLocalDatabase) : ViewModel() {
    private val _rooms = MutableStateFlow<List<RoomEntity>>(emptyList())
    val rooms: StateFlow<List<RoomEntity>> = _rooms

    init {
        viewModelScope.launch {
            db.roomEntityDAO().getAllRooms().collect { _rooms.value = it }
        }
    }

    fun addRoom(name: String) {
        viewModelScope.launch {
            db.roomEntityDAO().insertRoom(RoomEntity(name = name))
        }
    }

    fun getDeviceCount(roomId: Long, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = db.deviceDAO().getDeviceCountForRoom(roomId)
            onResult(count)
        }
    }

    fun deleteRoom(roomId: Int) {
        viewModelScope.launch {
            db.roomEntityDAO().deleteRoom(roomId)
        }
    }
}
