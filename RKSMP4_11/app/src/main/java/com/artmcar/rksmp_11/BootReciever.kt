package com.artmcar.rksmp_11

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean("enabled", false)
            if (enabled) { AlarmScheduler.scheduleNext(context) }
        }
    }
}