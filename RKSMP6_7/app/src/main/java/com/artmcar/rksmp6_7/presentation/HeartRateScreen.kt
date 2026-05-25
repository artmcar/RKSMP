package com.artmcar.rksmp6_7.presentation

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artmcar.rksmp6_7.data.BleDeviceInfo
import com.artmcar.rksmp6_7.data.ConnectionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HeartRateScreen(vm: HeartRateViewModel = viewModel()) {
    val scanState by vm.scan.collectAsState()
    val heartState by vm.heart.collectAsState()

    val permissions = rememberMultiplePermissionsState(requiredPermissions())
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(scanState.scanError) {
        scanState.scanError?.let {
            snackbar.showSnackbar(it)
            vm.clearScanError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Monitor") },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!permissions.allPermissionsGranted) {
                PermissionBlock(permissions)
            } else {
                HeartRateCard(heartState)
                ScanControls(
                    isScanning = scanState.isScanning,
                    connection = heartState.connection,
                    onStart = vm::startScan,
                    onStop = vm::stopScan,
                    onDisconnect = vm::disconnect
                )
                DeviceList(
                    devices = scanState.devices,
                    selectedAddress = heartState.selectedAddress,
                    onSelect = vm::connect
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionBlock(permissions: MultiplePermissionsState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Для сканирования BLE нужны разрешения Bluetooth и геолокации.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { permissions.launchMultiplePermissionRequest() }) {
            Text("Выдать разрешения")
        }
    }
}

@Composable
private fun HeartRateCard(state: HeartUiState) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val label = when (val c = state.connection) {
                is ConnectionState.Connected -> c.deviceName ?: "Подключено"
                ConnectionState.Connecting -> "Подключение..."
                ConnectionState.DiscoveringServices -> "Поиск сервисов..."
                ConnectionState.Disconnected -> "Не подключено"
                is ConnectionState.Error -> c.message
            }
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = state.bpm?.toString() ?: "--",
                    fontSize = 72.sp
                )
                Spacer(Modifier.width(8.dp))
                Text("bpm", style = MaterialTheme.typography.titleMedium)
            }

            if (state.rrMs.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("RR (мс): " + state.rrMs.joinToString(", "))
            }
            state.energyKJ?.let {
                Spacer(Modifier.height(4.dp))
                Text("Energy: $it кДж")
            }
        }
    }
}

@Composable
private fun ScanControls(
    isScanning: Boolean,
    connection: ConnectionState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDisconnect: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (isScanning) {
            Button(onClick = onStop, modifier = Modifier.weight(1f)) {
                Text("Остановить сканирование")
            }
        } else {
            Button(onClick = onStart, modifier = Modifier.weight(1f)) {
                Text("Сканировать пульсометры")
            }
        }
        val connected = connection is ConnectionState.Connected ||
                connection is ConnectionState.Connecting ||
                connection is ConnectionState.DiscoveringServices
        if (connected) {
            OutlinedButton(onClick = onDisconnect) { Text("Отключить") }
        }
    }
}

@Composable
private fun DeviceList(
    devices: List<BleDeviceInfo>,
    selectedAddress: String?,
    onSelect: (String) -> Unit
) {
    if (devices.isEmpty()) {
        Text(
            "Устройства не найдены. Нажмите «Сканировать».",
            style = MaterialTheme.typography.bodySmall
        )
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(devices, key = { it.address }) { d ->
            val selected = d.address == selectedAddress
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(d.address) },
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(d.name ?: "(без имени)", style = MaterialTheme.typography.titleSmall)
                        Text(d.address, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("${d.rssi} dBm", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private fun requiredPermissions(): List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
} else {
    listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}