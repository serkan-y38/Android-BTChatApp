package com.yilmaz.bt_chat.features.chat.presentation.screen.scan_devices_screen

import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothDeviceDomain

data class ScanDeviceState(
    val scannedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDomain> = emptyList(),
)
