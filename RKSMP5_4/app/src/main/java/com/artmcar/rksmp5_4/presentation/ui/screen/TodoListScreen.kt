package com.artmcar.rksmp5_4.presentation.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.artmcar.rksmp5_4.domain.model.TodoItem
import com.artmcar.rksmp5_4.presentation.ui.component.TodoItemCard

@Composable
fun TodoListScreen(
    todos: List<TodoItem>,
    useCompletedColor: Boolean,
    onToggle: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onClick: (Int) -> Unit
) {
    LazyColumn {
        items(todos) {
            TodoItemCard(
                todo = it,
                useCompletedColor = useCompletedColor,
                onClick = { onClick(it.id) },
                onToggle = { onToggle(it.id) }
            )
        }
    }
}

