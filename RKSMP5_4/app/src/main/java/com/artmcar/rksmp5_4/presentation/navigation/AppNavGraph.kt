package com.artmcar.rksmp5_4.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.artmcar.rksmp5_4.domain.model.TodoItem
import com.artmcar.rksmp5_4.presentation.ui.screen.TodoAddScreen
import com.artmcar.rksmp5_4.presentation.ui.screen.TodoListScreen
import com.artmcar.rksmp5_4.presentation.viewmodel.TodoViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: TodoViewModel,
    todos: List<TodoItem>,
    useCompletedColor: Boolean
) {
    NavHost(navController, startDestination = "list") {

        composable("list") {
            TodoListScreen(
                todos = todos,
                useCompletedColor = useCompletedColor,
                onToggle = viewModel::toggleTodo,
                onDelete = viewModel::deleteTodo,
                onClick = { navController.navigate("add") }
            )
        }

        composable("add") {
            TodoAddScreen(
                onSave = { t, d ->
                    viewModel.addTask(t, d)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}