package com.artmcar.rksmp6_7.data

import java.util.UUID

object HeartRateGattProfile {
    val SERVICE_HEART_RATE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    val CHAR_HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    val DESC_CLIENT_CHARACTERISTIC_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    data class Measurement(val bpm: Int, val energyExpendedKJ: Int?, val rrIntervalsMs: List<Int>)

    fun parseMeasurement(raw: ByteArray): Measurement? {
        if (raw.isEmpty()) return null
        val flags = raw[0].toInt() and 0xFF
        val is16Bit = (flags and 0x01) != 0
        val energyPresent = (flags and 0x08) != 0
        val rrPresent = (flags and 0x10) != 0

        var idx = 1
        val bpm = if (is16Bit) {
            if (raw.size < idx + 2) return null
            val v = ((raw[idx + 1].toInt() and 0xFF) shl 8) or (raw[idx].toInt() and 0xFF)
            idx += 2
            v
        } else {
            if (raw.size < idx + 1) return null
            val v = raw[idx].toInt() and 0xFF
            idx += 1
            v
        }

        var energy: Int? = null
        if (energyPresent && raw.size >= idx + 2) {
            energy = ((raw[idx + 1].toInt() and 0xFF) shl 8) or (raw[idx].toInt() and 0xFF)
            idx += 2
        }

        val rr = mutableListOf<Int>()
        if (rrPresent) {
            while (raw.size >= idx + 2) {
                val rawRr = ((raw[idx + 1].toInt() and 0xFF) shl 8) or (raw[idx].toInt() and 0xFF)
                rr.add((rawRr * 1000) / 1024)
                idx += 2
            }
        }
        return Measurement(bpm, energy, rr)
    }
}