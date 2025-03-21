import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun LEDScreen() {
    val brightness by LEDState.brightness.collectAsState()
    val red by LEDState.red.collectAsState()
    val green by LEDState.green.collectAsState()
    val blue by LEDState.blue.collectAsState()

    val ledScreenViewModel: LEDScreenViewModel = viewModel(factory = LEDScreenViewModelFactory())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit LED Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

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
    }
}
