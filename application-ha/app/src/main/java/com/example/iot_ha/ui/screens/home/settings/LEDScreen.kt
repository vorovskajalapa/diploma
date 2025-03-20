package com.example.iot_ha.ui.screens.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun LEDScreen() {
    var brightness by remember { mutableStateOf(50) }
    var red by remember { mutableStateOf(128) }
    var green by remember { mutableStateOf(128) }
    var blue by remember { mutableStateOf(128) }

    val color = Color(red, green, blue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Edit LED Settings", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Горизонтальный скролл для яркости
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            Slider(value = brightness.toFloat(), onValueChange = { brightness = it.roundToInt() },
                valueRange = 0f..100f)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("R" to red, "G" to green, "B" to blue).forEach { (label, value) ->
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = label, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Column(
                        modifier = Modifier
                            .height(150.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Slider(
                            value = value.toFloat(),
                            onValueChange = {
                                when (label) {
                                    "R" -> red = it.roundToInt()
                                    "G" -> green = it.roundToInt()
                                    "B" -> blue = it.roundToInt()
                                }
                            },
                            valueRange = 0f..255f
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color, shape = RoundedCornerShape(8.dp))
                .padding(top = 16.dp)
        )
    }
}
