package com.artmcar.rksmp_11

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {

    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "pill_channel",
            "Pill Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(context, "pill_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Напоминание")
            .setContentText("Время принять таблетку!")
            .setAutoCancel(true)
            .build()
        manager.notify(1001, notification)
        AlarmScheduler.scheduleNext(context)
    }
}