package com.artmcar.rksmp_8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var status by remember { mutableStateOf("Готово к работе") }
    var progress by remember { mutableStateOf(0f) }
    var resultOutputText by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(20.dp))
        if (isRunning) {
            LinearProgressIndicator(
                progress = { progress },
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(
            enabled = !isRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue,),
            onClick = {
                isRunning = true
                resultOutputText = ""
                progress = 0f
                val compress = OneTimeWorkRequestBuilder<FirstWorker>().build()
                val watermark = OneTimeWorkRequestBuilder<SecondWorker>().build()
                val upload = OneTimeWorkRequestBuilder<ThirdWorker>().build()
                workManager
                    .beginWith(compress)
                    .then(watermark)
                    .then(upload)
                    .enqueue()
                workManager.getWorkInfoByIdLiveData(compress.id)
                    .observeForever { info ->
                        if (info != null) {
                            if (info.state == WorkInfo.State.RUNNING) {
                                status = "Сжимаем фото..."
                                progress =
                                    info.progress.getInt("progress", 0) / 100f
                            }
                            if (info.state == WorkInfo.State.FAILED) {
                                isRunning = false
                                resultOutputText = "Ошибка при сжатии фото"
                            }
                        }
                    }
                workManager.getWorkInfoByIdLiveData(watermark.id)
                    .observeForever { info ->
                        if (info != null) {
                            if (info.state == WorkInfo.State.RUNNING) {
                                status = "Добавляем водяной знак..."
                                progress =
                                    info.progress.getInt("progress", 0) / 100f
                            }
                            if (info.state == WorkInfo.State.FAILED) {
                                isRunning = false
                                resultOutputText = "Ошибка при добавлении водяного знака"
                            }
                        }
                    }
                workManager.getWorkInfoByIdLiveData(upload.id)
                    .observeForever { info ->
                        if (info != null) {
                            if (info.state == WorkInfo.State.RUNNING) {
                                status = "Загружаем фото..."
                                progress =
                                    info.progress.getInt("progress", 0) / 100f
                            }
                            if (info.state == WorkInfo.State.SUCCEEDED) {
                                isRunning = false
                                progress = 1f
                                status = "Фото успешно загружено!"
                                resultOutputText =
                                    info.outputData.getString("result")
                                        ?: "Готово!"
                            }
                            if (info.state == WorkInfo.State.FAILED) {
                                isRunning = false
                                resultOutputText = "Ошибка загрузки"
                            }
                        }
                    }
            }
        ) {
            Text("Начать обработку и загрузку")
        }
        Spacer(Modifier.height(20.dp))
        Text(resultOutputText)
    }
}
