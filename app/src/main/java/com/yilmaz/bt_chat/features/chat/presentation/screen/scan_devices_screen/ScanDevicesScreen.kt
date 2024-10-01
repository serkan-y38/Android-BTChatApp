package com.yilmaz.bt_chat.features.chat.presentation.screen.scan_devices_screen

import android.bluetooth.BluetoothManager
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothDeviceDomain

@Composable
fun ScanDevicesScreen(
    navHostController: NavHostController,
    viewModel: ScanDevicesViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.errorMessage) {
        state.errorMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(key1 = state.isConnected) {
        if (state.isConnected) {
            Toast.makeText(
                context,
                "You're connected!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isConnecting -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Connecting...")
                    }
                }

                else -> {
                    BluetoothDeviceList(
                        pairedDevices = state.pairedDevices,
                        scannedDevices = state.scannedDevices,
                        onClick = { viewModel.connectToDevice(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = { viewModel.startScan() }) {
                            Text(text = "Start scan")
                        }
                        Button(onClick = { viewModel.stopScan() }) {
                            Text(text = "Stop scan")
                        }
                        Button(onClick = { viewModel.waitForIncomingConnections() }) {
                            Text(text = "Start server")
                        }
                    }
                }

            }

        }
    }
}

@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDeviceDomain>,
    scannedDevices: List<BluetoothDeviceDomain>,
    onClick: (BluetoothDeviceDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
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
                    .clickable { onClick(device) }
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
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }
    }
}