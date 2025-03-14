package com.example.iot_ha.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iot_ha.R

@Composable
fun DeviceCard(
    deviceId: Int,
    imageRes: Int,
    name: String,
    type: String,
    value: Any,
//    navController: NavHostController,
    onToggle: ((Int, Boolean) -> Unit)? = null,
    onSliderChange: ((Int, Float) -> Unit)? = null
) {
    var checked by remember { mutableStateOf(value as? Boolean ?: false) }
    var sliderValue by remember { mutableFloatStateOf(value as? Float ?: 0f) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
//            .clickable {
//                navController.navigate("device_details/$deviceId")
//            },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                when (type) {
                    "switch" -> {
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                onToggle?.invoke(deviceId, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4CAF50),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFF44336)
                            )
                        )
                    }

                    "slider" -> {
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                onSliderChange?.invoke(deviceId, it)
                            },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF8A9F9B),
                                activeTrackColor = Color(0xFFA6B6A9)
                            )
                        )
                    }

                    else -> {
                        Text(text = value.toString(), fontSize = 14.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MqttDeviceCardPreview() {
    Column {
        DeviceCard(
            deviceId = 1,
            imageRes = R.drawable.mqtt_logo,
            name = "Smart Light",
            type = "switch",
            value = true,
            onToggle = { deviceId, isChecked ->
                println("Устройство $deviceId: $isChecked")
            }
        )
        DeviceCard(
            deviceId = 2,
            imageRes = R.drawable.mqtt_logo,
            name = "Temperature",
            type = "text",
            value = "22°C"
        )
        DeviceCard(
            deviceId = 3,
            imageRes = R.drawable.mqtt_logo,
            name = "Dimmer",
            type = "slider",
            value = 50f,
            onSliderChange = { deviceId, value ->
                println("Устройство $deviceId: $value")
            }
        )
    }
}