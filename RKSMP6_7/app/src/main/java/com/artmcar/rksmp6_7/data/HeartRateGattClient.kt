package com.artmcar.rksmp6_7.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

class HeartRateGattClient(private val context: Context) {

    private val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = manager.adapter

    private val _connection = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connection: StateFlow<ConnectionState> = _connection.asStateFlow()

    private var gatt: BluetoothGatt? = null

    @SuppressLint("MissingPermission")
    fun connectAndObserve(address: String): Flow<HeartRateGattProfile.Measurement> = callbackFlow {
        val device: BluetoothDevice = adapter.getRemoteDevice(address)
        _connection.value = ConnectionState.Connecting

        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        _connection.value = ConnectionState.DiscoveringServices
                        g.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        _connection.value = ConnectionState.Disconnected
                        close()
                    }
                }
            }

            override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    _connection.value = ConnectionState.Error("Не удалось получить сервисы, Статус: $status)")
                    close()
                    return
                }
                val service = g.getService(HeartRateGattProfile.SERVICE_HEART_RATE)
                val char = service?.getCharacteristic(HeartRateGattProfile.CHAR_HEART_RATE_MEASUREMENT)
                if (char == null) {
                    _connection.value = ConnectionState.Error("Heart Rate сервис не найден")
                    close()
                    return
                }
                g.setCharacteristicNotification(char, true)
                val descriptor = char.getDescriptor(HeartRateGattProfile.DESC_CLIENT_CHARACTERISTIC_CONFIG)
                if (descriptor == null) {
                    _connection.value = ConnectionState.Error("CCC дескриптор отсутствует")
                    close()
                    return
                }
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    g.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    g.writeDescriptor(descriptor)
                }
                _connection.value = ConnectionState.Connected(device.name)
            }

            @Deprecated("Deprecated in Java")
            override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic) {
                @Suppress("DEPRECATION")
                val data = c.value ?: return
                handleMeasurement(c.uuid, data)
            }

            override fun onCharacteristicChanged(
                g: BluetoothGatt,
                c: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                handleMeasurement(c.uuid, value)
            }

            private fun handleMeasurement(uuid: java.util.UUID, data: ByteArray) {
                if (uuid != HeartRateGattProfile.CHAR_HEART_RATE_MEASUREMENT) return
                HeartRateGattProfile.parseMeasurement(data)?.let { trySend(it) }
            }
        }

        gatt = device.connectGatt(context, false, callback)

        awaitClose {
            runCatching { gatt?.disconnect() }
            runCatching { gatt?.close() }
            gatt = null
            _connection.value = ConnectionState.Disconnected
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connection.value = ConnectionState.Disconnected
    }
}
