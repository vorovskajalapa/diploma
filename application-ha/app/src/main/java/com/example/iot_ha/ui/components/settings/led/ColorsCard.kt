package com.example.iot_ha.ui.components.settings.led

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorsCard(
    red: Float,
    green: Float,
    blue: Float,
    onRedChange: (Float) -> Unit,
    onGreenChange: (Float) -> Unit,
    onBlueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Colors Status", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                Triple("Red", Color(red / 255f, 0f, 0f), onRedChange to red),
                Triple("Green", Color(0f, green / 255f, 0f), onGreenChange to green),
                Triple("Blue", Color(0f, 0f, blue / 255f), onBlueChange to blue)
            ).forEach { (label, color, setterWithValue) ->
                val (setter, value) = setterWithValue

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color)
                            .border(1.dp, Color.Black)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = label)
                        Slider(
                            value = value,
                            onValueChange = setter,
                            onValueChangeFinished = onValueChangeFinished,
                            valueRange = 0f..255f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
