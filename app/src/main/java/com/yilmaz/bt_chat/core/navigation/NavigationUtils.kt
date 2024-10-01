package com.yilmaz.bt_chat.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yilmaz.bt_chat.features.chat.presentation.screen.scan_devices_screen.ScanDevicesScreen

@Composable
fun SetUpNavigationGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavigationGraph.ScreenScanDevices
    ) {
        composable<NavigationGraph.ScreenScanDevices> {
            ScanDevicesScreen(navHostController)
        }
    }
}