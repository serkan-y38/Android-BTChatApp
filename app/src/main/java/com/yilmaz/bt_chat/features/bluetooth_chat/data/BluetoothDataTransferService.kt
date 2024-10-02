package com.yilmaz.bt_chat.features.bluetooth_chat.data

import android.bluetooth.BluetoothSocket
import com.yilmaz.bt_chat.features.bluetooth_chat.data.mappers.jsonToBluetoothMessageModel
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessageModel> {
        return flow {
            if (!socket.isConnected) return@flow

            val buffer = ByteArray(1024)

            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw IOException("Reading incoming data failed")
                }

                emit(
                    jsonToBluetoothMessageModel(
                        buffer.decodeToString(
                            endIndex = byteCount
                        )
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }
}