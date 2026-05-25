package com.artmcar.rksmp_6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.artmcar.rksmp_6.ui.theme.RKSMP_6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RKSMP_6Theme {
                TimerScreen()
            }
        }
    }
}

@Composable
fun TimerScreen() {

    val context = LocalContext.current
    var seconds by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = seconds,
            onValueChange = { seconds = it },
            label = { Text("Секунды") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {

            val intent = Intent(context, TimerService::class.java)
            intent.putExtra("seconds", seconds.toIntOrNull() ?: 0)

            context.startService(intent)
        }) {
            Text("Запустить таймер")
        }
    }
}

