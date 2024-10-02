package com.yilmaz.bt_chat.features.bluetooth_chat.data.mappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothDeviceModel

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceModel() = BluetoothDeviceModel(
    name = name,
    address = address
)
