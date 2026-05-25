package com.artmcar.rksmp6_7.data

data class BleDeviceInfo(
    val address: String,
    val name: String?,
    val rssi: Int
)

sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data object Connecting : ConnectionState
    data object DiscoveringServices : ConnectionState
    data class Connected(val deviceName: String?) : ConnectionState
    data class Error(val message: String) : ConnectionState
}