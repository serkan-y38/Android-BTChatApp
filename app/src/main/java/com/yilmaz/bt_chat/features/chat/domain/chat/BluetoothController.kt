package com.yilmaz.bt_chat.features.chat.domain.chat

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
    val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>

    val errors: SharedFlow<String>
    val isConnected: StateFlow<Boolean>

    fun startDiscovery()
    fun stopDiscovery()
    fun release()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult>
    fun closeConnection()
}