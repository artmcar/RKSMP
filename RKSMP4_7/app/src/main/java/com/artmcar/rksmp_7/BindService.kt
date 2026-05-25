package com.artmcar.rksmp_7

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BindService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val delaySeconds = 1000L
    private var job: Job? = null
    private var number: Int = 0

    inner class LocalBinder : Binder() {
        fun getService(): BindService = this@BindService
    }
    override fun onBind(intent: Intent?): IBinder {
        start()
        return binder
    }
    override fun onUnbind(intent: Intent?): Boolean {
        stop()
        return super.onUnbind(intent)
    }
    private fun start() {
        if (job?.isActive == true) return

        job = serviceScope.launch {
            while (true) {
                number = Random.nextInt(0, 101)
                delay(delaySeconds)
            }
        }
    }
    private fun stop() {
        job?.cancel()
        job = null
    }
    fun getNumber(): Int = number
}

