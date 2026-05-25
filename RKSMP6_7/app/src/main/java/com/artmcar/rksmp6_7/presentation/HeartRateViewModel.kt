package com.artmcar.rksmp6_7.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.artmcar.rksmp6_7.data.BleDeviceInfo
import com.artmcar.rksmp6_7.data.ConnectionState
import com.artmcar.rksmp6_7.data.HeartRateGattClient
import com.artmcar.rksmp6_7.data.HeartRateScanner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScanUiState(
    val isScanning: Boolean = false,
    val devices: List<BleDeviceInfo> = emptyList(),
    val scanError: String? = null
)

data class HeartUiState(
    val connection: ConnectionState = ConnectionState.Disconnected,
    val selectedAddress: String? = null,
    val bpm: Int? = null,
    val rrMs: List<Int> = emptyList(),
    val energyKJ: Int? = null
)

class HeartRateViewModel(app: Application) : AndroidViewModel(app) {

    private val scanner = HeartRateScanner(app)
    private val gattClient = HeartRateGattClient(app)

    private val _scan = MutableStateFlow(ScanUiState())
    val scan: StateFlow<ScanUiState> = _scan.asStateFlow()

    private val _heart = MutableStateFlow(HeartUiState())
    val heart: StateFlow<HeartUiState> = _heart.asStateFlow()

    private var scanJob: Job? = null
    private var connectJob: Job? = null

    init {
        viewModelScope.launch {
            gattClient.connection.collect { st ->
                _heart.update { it.copy(connection = st) }
            }
        }
    }

    fun startScan() {
        if (scanJob?.isActive == true) return
        if (!scanner.isBluetoothEnabled()) {
            _scan.update { it.copy(scanError = "Включите Bluetooth") }
            return
        }
        _scan.update { it.copy(isScanning = true, devices = emptyList(), scanError = null) }
        scanJob = viewModelScope.launch {
            scanner.scan()
                .catch { e -> _scan.update { it.copy(isScanning = false, scanError = e.message) } }
                .collect { device ->
                    _scan.update { state ->
                        val existing = state.devices.indexOfFirst { it.address == device.address }
                        val newList = if (existing >= 0) {
                            state.devices.toMutableList().also { it[existing] = device }
                        } else state.devices + device
                        state.copy(devices = newList.sortedByDescending { it.rssi })
                    }
                }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _scan.update { it.copy(isScanning = false) }
    }

    fun connect(address: String) {
        connectJob?.cancel()
        stopScan()
        _heart.update { HeartUiState(selectedAddress = address, connection = ConnectionState.Connecting) }
        connectJob = viewModelScope.launch {
            gattClient.connectAndObserve(address)
                .catch { e ->
                    _heart.update { it.copy(connection = ConnectionState.Error(e.message ?: "BLE error")) }
                }
                .collect { m ->
                    _heart.update {
                        it.copy(bpm = m.bpm, rrMs = m.rrIntervalsMs, energyKJ = m.energyExpendedKJ)
                    }
                }
        }
    }

    fun disconnect() {
        connectJob?.cancel()
        connectJob = null
        gattClient.disconnect()
        _heart.value = HeartUiState()
    }

    fun clearScanError() = _scan.update { it.copy(scanError = null) }

    override fun onCleared() {
        super.onCleared()
        stopScan()
        gattClient.disconnect()
    }
}