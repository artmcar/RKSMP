package com.artmcar.rksmp5_4.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.artmcar.rksmp5_4.domain.model.TodoItem

@Composable
fun TodoItemCard(
    todo: TodoItem,
    useCompletedColor: Boolean,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    val bg =
        if (todo.isCompleted && useCompletedColor)
            Color(0xFFD0F0C0)
        else
            MaterialTheme.colorScheme.surface

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Row(Modifier.padding(16.dp)) {
            Checkbox(todo.isCompleted, { onToggle() })
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    todo.title,
                    textDecoration = if (todo.isCompleted)
                        TextDecoration.LineThrough else null
                )
                todo.description?.let { Text(it) }
            }
        }
    }
}