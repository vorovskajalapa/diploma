package com.example.iot.ui.screens.home.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import com.example.iot.data.local.AppDatabase
import com.example.iot.data.local.device.state.DeviceState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

@Composable
fun DeviceDetailScreen(backStackEntry: NavBackStackEntry) {
    val deviceId = backStackEntry.arguments?.getString("deviceId")?.toIntOrNull() ?: -1
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val deviceState = remember { mutableStateOf<DeviceState?>(null) }
    val stateMap = remember { mutableStateOf<Map<String, Any>?>(null) }

    val deviceStateFlow = db.deviceStateDao().getDevicesStateByDeviceId(deviceId).collectAsState(initial = null)

    LaunchedEffect(deviceStateFlow.value) {
        deviceStateFlow.value?.let { state ->
            deviceState.value = state
            state.state?.let { json ->
                try {
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    stateMap.value = Gson().fromJson(json, type)
                } catch (e: Exception) {
                    stateMap.value = null
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Детали устройства",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        when {
            stateMap.value != null -> {
                stateMap.value!!.forEach { (key, value) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + ":",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = value.toString(),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            deviceState.value != null -> {
                Text(
                    text = "Некорректный формат состояния",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            else -> {
                Text(
                    text = "Устройство не найдено",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
