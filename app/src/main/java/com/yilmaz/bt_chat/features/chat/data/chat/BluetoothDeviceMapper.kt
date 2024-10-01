package com.yilmaz.bt_chat.features.chat.data.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain() = BluetoothDeviceDomain(
    name = name,
    address = address
)
