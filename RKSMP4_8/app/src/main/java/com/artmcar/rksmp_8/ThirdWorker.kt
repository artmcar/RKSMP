package com.artmcar.rksmp_8

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class ThirdWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val imagePath = inputData.getString("imagePath")
            ?: return Result.failure()
        for (i in 0..100 step 20) {
            delay(200)
            setProgress(workDataOf("progress" to i))
        }
        return Result.success(
            workDataOf(
                "result" to "Готово! Фото загружено: $imagePath"
            )
        )
    }
}