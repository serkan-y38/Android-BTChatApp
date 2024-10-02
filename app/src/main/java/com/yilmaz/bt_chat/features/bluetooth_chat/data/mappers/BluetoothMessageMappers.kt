package com.yilmaz.bt_chat.features.bluetooth_chat.data.mappers

import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun jsonToBluetoothMessageModel(json: String): BluetoothMessageModel {
    return Json.decodeFromString<BluetoothMessageModel>(json).also {
        BluetoothMessageModel(
            message = it.message,
            senderName = it.senderName,
            isFromLocalUser = false
        )
    }
}

fun messageModelToByteArray(model: BluetoothMessageModel): ByteArray {
    return Json.encodeToString(
        BluetoothMessageModel(
            message = model.message,
            senderName = model.senderName,
            isFromLocalUser = model.isFromLocalUser
        )
    ).encodeToByteArray()
}