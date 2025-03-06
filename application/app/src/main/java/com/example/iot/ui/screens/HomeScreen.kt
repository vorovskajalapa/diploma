package com.example.iot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iot.data.local.AppDatabase
import com.example.iot.ui.screens.home.DevicesScreen
import com.example.iot.ui.screens.home.RoomsScreen
import com.example.iot.ui.screens.home.SettingsScreen
import com.example.iot.ui.viewmodel.HomeViewModel
import com.example.iot.ui.viewmodel.factory.HomeViewModelFactory

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Устройства", "Комнаты", "Настройки")

    val db = AppDatabase.getInstance(LocalContext.current)
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(db))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFF5F5DC))
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(tabs) { index, title ->
                TabButton(
                    title = title,
                    isSelected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> DevicesScreen()
            1 -> RoomsScreen()
            2 -> SettingsScreen()
        }
    }
}

@Composable
fun TabButton(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .height(45.dp)
            .padding(vertical = 4.dp)
    ) {
        Text(title)
    }
}




