package com.example.iot_ha.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.iot_ha.R
import com.example.iot_ha.data.local.device.DeviceState
import com.example.iot_ha.ui.components.devices.DeviceCard
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.utils.Constants
import com.example.iot_ha.utils.toBooleanState

@Composable
fun DevicesScreen(navHostController: NavHostController, devicesViewModel: DevicesViewModel) {

    val devices by devicesViewModel.devices.collectAsState()
    val switchDevices by devicesViewModel.getDevicesByTypeFlow("switch").collectAsState()
    val deviceState by DeviceState.devicesData.collectAsState()


//    val predefinedSelectDevice = remember {
//        object {
//            val id = -1
//            val friendlyName = "Выбор режима"
//            val type = "select"
//            val value = "Авто"
//            val options = listOf("Авто", "Ручной", "Выключен")
//        }
//    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
//            DeviceCard(
//                deviceId = predefinedSelectDevice.id,
//                imageRes = R.drawable.mqtt_logo,
//                name = predefinedSelectDevice.friendlyName,
//                type = predefinedSelectDevice.type,
//                value = predefinedSelectDevice.value,
//                options = predefinedSelectDevice.options,
//                navController = navHostController,
//                onSelectChange = { option ->
//                    devicesViewModel.onSelectChange(predefinedSelectDevice.id, option)
//                }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))

            switchDevices.forEach { device ->

                val deviceData = deviceState[device.id]

                DeviceCard(
                    deviceId = device.id,
                    imageRes = R.drawable.mqtt_logo,
                    name = device.friendlyName,
                    type = Constants.SWITCH_TYPE,
                    value = (deviceData?.get("state") as? String)?.toBooleanState() ?: false,
                    navController = navHostController,
                    onToggle = { state ->
                        devicesViewModel.onToggle(device.id, state)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }


        }
    }
}

