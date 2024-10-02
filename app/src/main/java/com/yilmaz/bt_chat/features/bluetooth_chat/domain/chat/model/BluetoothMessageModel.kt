package com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model

import kotlinx.serialization.Serializable

@Serializable
data class BluetoothMessageModel(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)