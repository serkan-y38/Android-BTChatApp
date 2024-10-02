package com.yilmaz.bt_chat.features.chat.presentation.screen.home_screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConnectingDialog(onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = "Connecting")
        },
        text = {
            Text(text = "Please wait while connecting to selected device")
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StartServerDialog(onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = "Server started")
        },
        text = {
            Text(text = "Waiting for connection request from another device")
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        }
    )
}