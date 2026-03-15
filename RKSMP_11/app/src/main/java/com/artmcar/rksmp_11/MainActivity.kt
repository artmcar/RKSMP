package com.artmcar.rksmp_11

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artmcar.rksmp_11.ui.theme.RKSMP_11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RKSMP_11Theme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun MainScreen() {
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            permissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
    var enabled by remember { mutableStateOf(prefs.getBoolean("enabled", false)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Напоминание о таблетке", fontSize = 24.sp)
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        if (enabled) Color.Green else Color.Gray,
                        shape = CircleShape
                    )
            )
            Spacer(Modifier.width(8.dp))
            Text(if (enabled) "Включено" else "Выключено")
        }
        Spacer(Modifier.height(30.dp))
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue,),
            onClick = {

                if (!enabled) {
                    AlarmScheduler.scheduleNext(context)
                } else {
                    AlarmScheduler.cancel(context)
                }
                enabled = !enabled
                prefs.edit()
                    .putBoolean("enabled", enabled)
                    .apply()
            }
        ) {
            Text(
                if (enabled)
                    "Выключить напоминание"
                else
                    "Включить напоминание"
            )
        }
    }
}
