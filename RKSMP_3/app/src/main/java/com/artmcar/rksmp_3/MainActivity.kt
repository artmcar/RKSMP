package com.artmcar.rksmp_3

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.artmcar.rksmp_3.ui.theme.RKSMP_3Theme
import com.artmcar.rksmp_3.ui.theme.Repository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RKSMP_3Theme {
                var repository by remember {
                    mutableStateOf<List<Repository>>(emptyList())
                }
                val scope = rememberCoroutineScope()
                LaunchedEffect(true) {
                    repository = loadRepository(this@MainActivity)
                }
                RepositorySearchScreen(repository)
            }
        }
    }
}

suspend fun loadRepository(context: Context): List<Repository> = withContext(Dispatchers.IO){
    val json = context.assets.open("github_repos.json").bufferedReader().use{it.readText()}
    val type = object: TypeToken<List<Repository>>() {}.type
    Gson().fromJson(json, type)
}

@Composable
fun RepositorySearchScreen(repository: List<Repository>){
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<Repository>>(emptyList()) }
    var loading by remember { ( mutableStateOf(false))}
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    Column(Modifier.padding(32.dp)){
        TextField(
            value = query,
            onValueChange = {
                query = it
                searchJob?.cancel()
                searchJob = scope.launch {
                    loading = true
                    delay(500)
                    result = repository.filter { it.full_name.contains(query, ignoreCase = true) }
                    loading = false
                }
            },
            label = {Text("Поле поиска")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        if(loading){
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        LazyColumn {
            items(result) { repository ->
                Column (modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
                ) {
                    Text(repository.full_name, fontWeight = FontWeight.Bold)
                    Text(repository.description)
                    Text("Stars: ${repository.stargazers_count}")
                    Text("Language: ${repository.language}", fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}