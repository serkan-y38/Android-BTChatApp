package com.yilmaz.bt_chat.features.bluetooth_chat.data.mappers

import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun jsonToBluetoothMessageModel(json: String): BluetoothMessageModel {
    val m: BluetoothMessageModel = Json.decodeFromString(json)
    return BluetoothMessageModel(
        message = m.message,
        senderName = m.senderName,
        isFromLocalUser = false
    )
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