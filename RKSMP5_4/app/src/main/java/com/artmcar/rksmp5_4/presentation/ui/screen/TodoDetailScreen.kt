package com.artmcar.rksmp5_4.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.artmcar.rksmp5_4.domain.model.TodoItem

@Composable
fun TodoDetailScreen(todo: TodoItem, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = todo.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = todo.description ?: "")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = if (todo.isCompleted) "Выполнено ✓" else "Не выполнено", color = if (todo.isCompleted) Color.Green else Color.Red)
    }
}