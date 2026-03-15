import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artmcar.rksmp_9.R
import kotlinx.coroutines.delay

class CollectFinalWeatherInfoWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        updateNotification("Загружаем погоду для 3 городов…")
        delay(2000)
        updateNotification(
            "Готово: Москва и Лондон, Нью-Йорк в процессе..."
        )
        delay(2000)
        updateNotification(
            "Все данные получены, формируем отчёт..."
        )
        val temps = inputData.keyValueMap
            .filter { it.key.startsWith("temp_") }
            .map { it.value as Int }
        val avgTemp = temps.average().toInt()
        delay(2000)
        showFinalNotification(avgTemp)
        return Result.success()
    }
    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(
            applicationContext,
            "weather_channel"
        )
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .build()
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
    private fun showFinalNotification(avgTemp: Int) {
        val notification = NotificationCompat.Builder(
            applicationContext,
            "weather_channel"
        )
            .setContentTitle("Прогноз погоды")
            .setContentText("Отчёт готов! Средняя температура $avgTemp°C")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(false)
            .setAutoCancel(false)
            .build()
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}
