package com.example.iot_ha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.ui.components.common.TabButton
import com.example.iot_ha.ui.screens.home.DevicesScreen
import com.example.iot_ha.ui.screens.home.RoomsScreen
import com.example.iot_ha.ui.screens.home.SettingsScreen
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.utils.Constants

@Composable
fun HomeScreen(navHostController: NavHostController, devicesViewModel: DevicesViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val db = RoomLocalDatabase.getInstance(LocalContext.current)
//    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(db))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(Constants.TABS_LIST) { index, title ->
                TabButton(
                    title = title,
                    isSelected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> DevicesScreen(
                navHostController = navHostController,
                devicesViewModel = devicesViewModel
            )

            1 -> RoomsScreen()
            2 -> SettingsScreen()
        }
    }
}






