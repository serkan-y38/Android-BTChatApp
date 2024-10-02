package com.yilmaz.bt_chat.features.chat.presentation.screen.home_screen

import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothDeviceDomain
import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothMessage

data class HomeState(
    val scannedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isServerStarted:Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()
)
