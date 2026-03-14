package com.artmcar.rksmp_8

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class FirstWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            for (i in 0..100 step 20) {
                delay(200)
                setProgress(workDataOf("progress" to i))
            }
            val compressedImage = "comp_img.jpg"
            Result.success(workDataOf("imagePath" to compressedImage))
        } catch (e: Exception) {
            Result.failure()
        }
    }
}