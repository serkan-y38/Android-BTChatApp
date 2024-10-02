package com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat

import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel

sealed interface ConnectionResult {
    data object ConnectionEstablished : ConnectionResult
    data class Error(val message: String) : ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessageModel): ConnectionResult
}