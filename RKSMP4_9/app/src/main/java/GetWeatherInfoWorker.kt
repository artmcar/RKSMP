import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
class GetWeatherInfoWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val city = inputData.getString("city") ?: return Result.failure()
        val temp = (-20..30).random()
        delay(2000)
        return Result.success(
            workDataOf(
                "city_$city" to city,
                "temp_$city" to temp
            )
        )
    }
}