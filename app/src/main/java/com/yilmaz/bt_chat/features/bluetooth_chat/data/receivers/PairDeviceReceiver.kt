package com.yilmaz.bt_chat.features.bluetooth_chat.data.receivers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class PairDeviceReceiver(
    private val onPairRequest : () -> Unit
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(p0: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_PAIRING_REQUEST -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }

                val pin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_PAIRING_KEY,
                        BluetoothDevice::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_PAIRING_KEY)
                }


                device?.let { d ->
                    pin?.let { p ->

                        val pinBytes: ByteArray = p.toString().toByteArray(Charsets.UTF_8)

                        d.setPin(pinBytes)
                        d.setPairingConfirmation(true)

                        onPairRequest()
                    }
                }
            }
        }
    }
}