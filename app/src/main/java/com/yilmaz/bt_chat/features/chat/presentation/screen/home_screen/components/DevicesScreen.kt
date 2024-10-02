package com.yilmaz.bt_chat.features.chat.presentation.screen.home_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothDeviceDomain

@Composable
fun DevicesScreen(
    pairedDevices: List<BluetoothDeviceDomain>,
    scannedDevices: List<BluetoothDeviceDomain>,
    onPairedDevicesItemClick: (BluetoothDeviceDomain) -> Unit,
    onScannedDevicesItemClick: (BluetoothDeviceDomain) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onStartServer: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Paired Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(pairedDevices) { device ->
                Text(
                    text = device.name ?: "(No name)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPairedDevicesItemClick(device) }
                        .padding(16.dp)
                )
            }

            item {
                Text(
                    text = "Scanned Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(scannedDevices) { device ->
                Text(
                    text = device.name ?: "(No name)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onScannedDevicesItemClick(device) }
                        .padding(16.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { onStartScan() }) {
                Text(text = "Start scan")
            }
            Button(onClick = { onStopScan() }) {
                Text(text = "Stop scan")
            }
            Button(onClick = { onStartServer() }) {
                Text(text = "Start server")
            }
        }
    }
}