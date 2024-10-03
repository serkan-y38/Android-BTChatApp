package com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.yilmaz.bt_chat.features.bluetooth_chat.domain.chat.model.BluetoothMessageModel
import com.yilmaz.bt_chat.features.bluetooth_chat.presentation.screen.home_screen.HomeState

@Composable
fun ChatScreen(
    state: HomeState,
    onDisconnectDevice: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val message = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.connectedDeviceName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onDisconnectDevice() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect"
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.messages) { message ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MessageItem(
                        message = message,
                        modifier = Modifier
                            .align(
                                if (message.isFromLocalUser) Alignment.End else Alignment.Start
                            )
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message")
                },
                trailingIcon = {
                    IconButton(onClick = {
                        onSendMessage(message.value)
                        message.value = ""
                        keyboardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message"
                        )
                    }
                }
            )

        }
    }
}

@Composable
fun MessageItem(
    message: BluetoothMessageModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 16.dp else 0.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = if (message.isFromLocalUser) 0.dp else 16.dp
                )
            )
            .background(
                color = if (message.isFromLocalUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondaryContainer
            )
            .padding(12.dp)
    ) {
        Log.i(
            "sender -> message",
            message.senderName + " - " + message.message
        )
        Text(
            text = message.message,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }
}