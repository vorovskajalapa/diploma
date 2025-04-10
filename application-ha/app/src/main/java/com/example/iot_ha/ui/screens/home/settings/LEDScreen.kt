package com.example.iot_ha.ui.screens.home.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iot_ha.data.local.led.LEDState
import com.example.iot_ha.ui.components.settings.led.BrigtnessCard
import com.example.iot_ha.ui.components.settings.led.ColorsCard
import com.example.iot_ha.ui.viewmodels.LEDScreenViewModel
import com.example.iot_ha.ui.viewmodels.factory.LEDScreenViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LEDScreen() {
    val brightness by LEDState.brightness.collectAsState()
    val red by LEDState.red.collectAsState()
    val green by LEDState.green.collectAsState()
    val blue by LEDState.blue.collectAsState()

    val ledScreenViewModel: LEDScreenViewModel = viewModel(factory = LEDScreenViewModelFactory())

    var selectedMode by remember { mutableStateOf("AUTO") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),     
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit LED Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedMode,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("AUTO") },
                    onClick = {
                        ledScreenViewModel.setAUTOMode()
                        selectedMode = "AUTO"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("MANUAL") },
                    onClick = {
                        selectedMode = "MANUAL"
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedMode == "MANUAL") {
            BrigtnessCard(
                title = "Brightness",
                color = Color(brightness / 255f, brightness / 255f, brightness / 255f),
                value = brightness,
                onValueChange = { LEDState.setBrightness(it) },
                onValueChangeFinished = { ledScreenViewModel.sendLEDStatus() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColorsCard(
                red = red,
                green = green,
                blue = blue,
                onRedChange = { LEDState.setRed(it) },
                onGreenChange = { LEDState.setGreen(it) },
                onBlueChange = { LEDState.setBlue(it) },
                onValueChangeFinished = { ledScreenViewModel.sendLEDStatus() }
            )
        } else {
            Text(text = "LED settings are in AUTO mode. No adjustments available.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
