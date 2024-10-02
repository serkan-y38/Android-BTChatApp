package com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat

import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothDeviceModel
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDeviceModel>>
    val pairedDevices: StateFlow<List<BluetoothDeviceModel>>
    val errors: SharedFlow<String>
    val isConnected: StateFlow<Boolean>

    fun startScan()

    fun stopScan()

    fun release()

    fun startBluetoothServer(): Flow<ConnectionResult>

    fun connectToDevice(device: BluetoothDeviceModel): Flow<ConnectionResult>

    fun closeConnection()

    suspend fun sendMessage(message: String): BluetoothMessageModel?
}