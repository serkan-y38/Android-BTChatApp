package com.yilmaz.bt_chat.features.bluetooth_chat.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import com.yilmaz.bt_chat.features.bluetooth_chat.data.mappers.toBluetoothDeviceModel
import com.yilmaz.bt_chat.features.bluetooth_chat.data.mappers.messageModelToByteArray
import com.yilmaz.bt_chat.features.bluetooth_chat.data.receivers.BluetoothStateReceiver
import com.yilmaz.bt_chat.features.bluetooth_chat.data.receivers.FoundDeviceReceiver
import com.yilmaz.bt_chat.features.bluetooth_chat.data.receivers.PairDeviceReceiver
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.BluetoothController
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothDeviceModel
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.ConnectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null
    private var dataTransferService: BluetoothDataTransferService? = null

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())

    override val scannedDevices: StateFlow<List<BluetoothDeviceModel>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())

    override val pairedDevices: StateFlow<List<BluetoothDeviceModel>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()

    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val _isConnected = MutableStateFlow(false)

    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver(
        onDeviceFound = { device ->
            _scannedDevices.update { devices ->
                val newDevice = device.toBluetoothDeviceModel()
                if (newDevice in devices) devices else devices + newDevice
            }
        }
    )

    private val bluetoothStateReceiver = BluetoothStateReceiver(
        onStateChanged = { isConnected, bluetoothDevice ->
            if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
                _isConnected.update { isConnected }
            }
        }
    )

    private val pairDeviceReceiver = PairDeviceReceiver(
        onPairRequest = {}
    )

    init {
        updatePairedDevices()
        registerConnectionStateReceiver()
    }

    override fun startScan() {
        if (!hasBluetoothScanPermission()) return
        registerFoundDeviceReceiver()
        updatePairedDevices()
        registerPairDeviceReceiver()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopScan() {
        if (!hasBluetoothScanPermission()) return
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun pair(address: String) {
        if (!hasBluetoothConnectPermission()) return

        val device = bluetoothAdapter?.getRemoteDevice(address)
        device?.let {
            try {
                device.createBond()
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    _errors.emit("Failed to pair with device: ${e.message}")
                }
            }
        } ?: run {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Device not found with address: $address")
            }
        }
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            checkBluetoothConnectPermission()

            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                SERVICE_NAME,
                UUID.fromString(SERVICE_UUID)
            )

            var condition = true

            while (condition) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    condition = false
                    null
                }

                currentClientSocket?.let { socket ->
                    emit(
                        ConnectionResult.ConnectionEstablished(
                            currentClientSocket!!.remoteDevice.name
                        )
                    )

                    val service = BluetoothDataTransferService(socket)

                    currentServerSocket?.close()
                    dataTransferService = service

                    emitAll(
                        service
                            .listenForIncomingMessages()
                            .map { message ->
                                ConnectionResult.TransferSucceeded(message)
                            }
                    )
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceModel): Flow<ConnectionResult> {
        return flow {
            checkBluetoothConnectPermission()
            stopScan()

            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )

            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(
                        ConnectionResult.ConnectionEstablished(
                            currentClientSocket!!.remoteDevice.name
                        )
                    )

                    BluetoothDataTransferService(socket).also { service ->
                        dataTransferService = service
                        emitAll(
                            service.listenForIncomingMessages()
                                .map { message ->
                                    ConnectionResult.TransferSucceeded(message)
                                }
                        )
                    }
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        context.unregisterReceiver(pairDeviceReceiver)
        closeConnection()
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override suspend fun sendMessage(message: String): BluetoothMessageModel? {
        checkBluetoothConnectPermission()

        if (dataTransferService == null) return null

        val bluetoothMessageModel = BluetoothMessageModel(
            message = message,
            senderName = bluetoothAdapter?.name ?: "Unknown name",
            isFromLocalUser = true
        )

        dataTransferService?.sendMessage(messageModelToByteArray(bluetoothMessageModel))

        return bluetoothMessageModel
    }

    private fun updatePairedDevices() {
        if (!hasBluetoothConnectPermission()) return

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceModel() }
            ?.also { devices -> _pairedDevices.update { devices } }
    }

    private fun registerConnectionStateReceiver() {
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    private fun registerFoundDeviceReceiver() {
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
    }

    private fun registerPairDeviceReceiver() {
        context.registerReceiver(
            pairDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST)
        )
    }

    private fun checkBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                error("No BLUETOOTH_CONNECT permission")
                throw SecurityException()
            }
        } else {
            if (!hasPermission(Manifest.permission.BLUETOOTH)) {
                error("No BLUETOOTH permission")
                throw SecurityException()
            }
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                error("No BLUETOOTH_CONNECT permission")
                hasPermission = false
            }
        } else {
            if (!hasPermission(Manifest.permission.BLUETOOTH)) {
                error("No BLUETOOTH permission")
                hasPermission = false
            }
        }
        return hasPermission
    }

    private fun hasBluetoothScanPermission(): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                error("No BLUETOOTH_SCAN permission")
                hasPermission = false
            }
        } else {
            if (!hasPermission(Manifest.permission.BLUETOOTH)) {
                error("No BLUETOOTH permission")
                hasPermission = false
            }
        }
        return hasPermission
    }

    private fun hasPermission(permission: String) =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    private fun error(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _errors.emit(text)
        }
    }

    companion object {
        const val SERVICE_UUID = "27b7d1da-08c7-4505-a6d1-2459987e5e2d"
        const val SERVICE_NAME = "chat_service"
    }
}
