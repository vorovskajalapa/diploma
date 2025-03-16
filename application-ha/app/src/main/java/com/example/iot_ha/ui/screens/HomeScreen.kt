package com.example.iot_ha.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.iot_ha.ui.components.common.TabButton
import com.example.iot_ha.ui.screens.home.DevicesScreen
import com.example.iot_ha.ui.screens.home.RoomsScreen
import com.example.iot_ha.ui.screens.home.SettingsScreen
import com.example.iot_ha.ui.viewmodels.shared.DevicesViewModel
import com.example.iot_ha.ui.viewmodels.shared.RoomsViewModel
import com.example.iot_ha.utils.Constants

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    devicesViewModel: DevicesViewModel,
    roomsViewModel: RoomsViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var previousTab by remember { mutableIntStateOf(0) }

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
                    onClick = {
                        previousTab = selectedTab
                        selectedTab = index
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState > previousTab) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "Tab Animation"
        ) { tab ->
            when (tab) {
                0 -> DevicesScreen(
                    navHostController = navHostController,
                    devicesViewModel = devicesViewModel
                )

                1 -> RoomsScreen(
                    navHostController = navHostController,
                    roomsViewModel = roomsViewModel
                )

                2 -> SettingsScreen()
            }
        }
    }
}