package com.artmcar.rksmp5_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.artmcar.rksmp5_4.data.local.TodoDatabase
import com.artmcar.rksmp5_4.data.local.TodoJsonDataSource
import com.artmcar.rksmp5_4.data.repository.TodoRepositoryImpl
import com.artmcar.rksmp5_4.presentation.navigation.AppNavGraph
import com.artmcar.rksmp5_4.presentation.theme.RKSMP5_4Theme
import com.artmcar.rksmp5_4.presentation.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = TodoDatabase.getDatabase(this)
        val repo = TodoRepositoryImpl(
            TodoJsonDataSource(this),
            db.todoDao()
        )

        val dataStore = (application as TodoApplication).dataStore
        val viewModel = TodoViewModel(repo, dataStore)

        setContent {
                RKSMP5_4Theme {
                val navController = rememberNavController()

                val todos by viewModel.todos.collectAsState()
                val useColor by viewModel.useCompletedColor.collectAsState()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Todo") },
                            actions = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Цвет завершённых")
                                    Switch(
                                        checked = useColor,
                                        onCheckedChange = {
                                            viewModel.toggleCompletedColor()
                                        }
                                    )
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { navController.navigate("add") }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        AppNavGraph(
                            navController,
                            viewModel,
                            todos,
                            useColor
                        )
                    }
                }
            }
        }
    }
}