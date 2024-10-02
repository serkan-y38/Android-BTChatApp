package com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen

import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothDeviceModel
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel

data class HomeState(
    val scannedDevices: List<BluetoothDeviceModel> = emptyList(),
    val pairedDevices: List<BluetoothDeviceModel> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isServerStarted: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessageModel> = emptyList()
)
