package com.artmcar.rksmp_9

import CollectFinalWeatherInfoWorker
import GetWeatherInfoWorker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val channel = NotificationChannel(
            "weather_channel",
            "Weather Updates",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(){
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue,),
            onClick = {
                val cities = listOf("Москва", "Лондон", "Нью-Йорк")
                val workers = cities.map { city ->
                    OneTimeWorkRequestBuilder<GetWeatherInfoWorker>()
                        .setInputData(workDataOf("city" to city))
                        .build()
                }
                val finalWorker =
                    OneTimeWorkRequestBuilder<CollectFinalWeatherInfoWorker>()
                        .build()

                workManager
                    .beginWith(workers)
                    .then(finalWorker)
                    .enqueue()
            }
        ) {
            Text("Собрать прогноз")
        }
    }
}

