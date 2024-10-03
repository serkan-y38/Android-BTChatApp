package com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen.components.ChatScreen
import com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen.components.DevicesScreen
import com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen.components.ConnectingDialog
import com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen.components.StartServerDialog

@Composable
fun HomeScreen(
    @Suppress("UNUSED_PARAMETER") navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val hostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = state.errorMessage) {
        state.errorMessage?.let { message ->
            hostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                state.isConnecting -> {
                    ConnectingDialog(onDismiss = {
                        viewModel.disconnectFromDevice()
                    })
                }

                state.isServerStarted -> {
                    StartServerDialog(onDismiss = {
                        viewModel.disconnectFromDevice()
                    })
                }

                state.isConnected -> {
                    ChatScreen(
                        state = viewModel.state.collectAsState().value,
                        onDisconnectDevice = { viewModel.disconnectFromDevice() },
                        onSendMessage = { message ->
                            viewModel.sendMessage(message)
                        }
                    )
                }

                else -> {
                    DevicesScreen(
                        pairedDevices = state.pairedDevices,
                        scannedDevices = state.scannedDevices,
                        onPairedDevicesItemClick = { device -> viewModel.connectToDevice(device) },
                        onScannedDevicesItemClick = { device -> viewModel.pair(device.address) },
                        onStartScan = { viewModel.startScan() },
                        onStopScan = { viewModel.stopScan() },
                        onStartServer = {
                            viewModel.startBluetoothServer()
                        }
                    )
                }
            }
        }
    }
}

