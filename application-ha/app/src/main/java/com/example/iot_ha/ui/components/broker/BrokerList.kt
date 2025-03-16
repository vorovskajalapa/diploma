package com.example.iot_ha.ui.components.broker


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iot_ha.data.local.broker.Broker

@Composable
fun BrokerList(
    brokers: List<Broker>,
    onDelete: (Broker) -> Unit,
    onLogin: (Broker) -> Unit
) {
    brokers.lastOrNull()?.let { broker ->
        Text("Recently used broker:", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(1.dp))
        BrokerItem(
            broker = broker,
            onDelete = { onDelete(broker) },
            onLogin = { onLogin(broker) }
        )
    }
}