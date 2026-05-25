package com.artmcar.rksmp_5

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.artmcar.rksmp_5.ui.theme.RKSMP_5Theme


class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            RKSMP_5Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {

    val context = LocalContext.current
    var seconds by remember { mutableStateOf(0) }

    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                seconds = intent?.getIntExtra("timePassed", 0) ?: 0
            }
        }
    }

    DisposableEffect(Unit) {

        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(
                receiver,
                IntentFilter("TIMER_UPDATE"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter("TIMER_UPDATE"),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = seconds.toString(),
            fontSize = 64.sp
        )

        Spacer(Modifier.height(24.dp))

        Column {

            Button(onClick = {
                val intent = Intent(context, TimeCounterService::class.java)
                context.startForegroundService(intent)
            }) {
                Text("Старт")
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                context.stopService(Intent(context, TimeCounterService::class.java))
            }) {
                Text("Стоп")
            }
        }
    }
}