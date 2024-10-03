package com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.BluetoothController
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothDeviceModel
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val btController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())

    val state = combine(
        btController.scannedDevices,
        btController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if (state.isConnected) state.messages else emptyList()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    private var deviceConnectionJob: Job? = null

    init {
        isConnected()
        getErrors()
    }

    private fun isConnected() {
        btController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)
    }

    private fun getErrors() {
        btController.errors.onEach { error ->
            _state.update {
                it.copy(
                    errorMessage = error
                )
            }
        }.launchIn(viewModelScope)
    }

    fun startScan() {
        btController.startScan()
    }

    fun stopScan() {
        btController.stopScan()
    }

    fun startBluetoothServer() {
        _state.update { it.copy(isServerStarted = true) }
        deviceConnectionJob = btController
            .startBluetoothServer()
            .listen()
    }

    fun connectToDevice(device: BluetoothDeviceModel) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = btController
            .connectToDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        btController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                isServerStarted = false,
                errorMessage = null,
                connectedDeviceName = ""
            )
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val bluetoothMessage = btController.sendMessage(message)
            if (bluetoothMessage != null) {
                _state.update {
                    it.copy(
                        messages = it.messages + bluetoothMessage
                    )
                }
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            isServerStarted = false,
                            errorMessage = null,
                            connectedDeviceName = result.connectedDeviceName
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + result.message
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
            .catch { e ->
                btController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                        isServerStarted = false,
                        errorMessage = e.message
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        btController.release()
    }
}