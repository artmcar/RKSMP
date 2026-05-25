package com.artmcar.rksmp_4

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.artmcar.rksmp_4.ui.theme.RKSMP_4Theme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RKSMP_4Theme {
                SocialFeedScreen()
            }
        }
    }
}

suspend fun loadPosts(context: Context): List<Post> =
    withContext(Dispatchers.IO) {
        val json = context.assets.open("social_posts.json")
            .bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<Post>>() {}.type
        Gson().fromJson(json, type)
    }

suspend fun loadAllComments(context: Context): List<Comment> =
    withContext(Dispatchers.IO) {
        val json = context.assets.open("comments.json")
            .bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<Comment>>() {}.type
        Gson().fromJson(json, type)
    }
suspend fun loadPostData(
    post: Post,
    allComments: List<Comment>
): PostState = supervisorScope {

    val avatarDeferred = async {
        try {
            delay(500)
            post.avatarUrl
        } catch (e: Exception) { null }
    }
    val commentsDeferred = async {
        try {
            delay(1000)
            allComments.filter { it.postId == post.id }
        } catch (e: Exception) { emptyList<Comment>() }
    }
    val avatar = avatarDeferred.await()
    val comments = commentsDeferred.await()
    if (avatar == null) {
        PostState.Error
    } else {
        PostState.Ready(
            avatar = avatar,
            comments = comments
        )
    }
}

@Composable
fun PostCard(post: Post, state: PostState) {
    Card(Modifier.padding(16.dp).fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {

            Text(post.title, fontWeight = FontWeight.Bold)
            Text(post.body)

            Spacer(Modifier.height(8.dp))

            when (state) {
                PostState.Loading -> CircularProgressIndicator()

                is PostState.Ready -> {
                    Text("Аватарка: ${state.avatar}")
                    state.comments.forEach {
                        Text("Reply (${it.name}): ${it.body}")
                    }
                }

                PostState.Error -> Text("Возникла ошибка при загрузке аватарки")
            }
        }
    }
}
@Composable
fun SocialFeedScreen() {
    val context = LocalContext.current
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var postStates by remember { mutableStateOf<Map<Int, PostState>>(emptyMap()) }
    val scope = rememberCoroutineScope()
    var loadJob by remember { mutableStateOf<Job?>(null) }
    fun loadFeed() {
        loadJob?.cancel()
        loadJob = scope.launch {
            postStates = emptyMap()
            posts = loadPosts(context)
            val comments = loadAllComments(context)
            posts.forEach { post ->
                postStates = postStates + (post.id to PostState.Loading)
                launch {
                    val state = loadPostData(post, comments)
                    postStates = postStates.toMutableMap().apply {
                        put(post.id, state)
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) { loadFeed() }
    Column {
        Button(
            onClick = { loadFeed() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Magenta,
                contentColor = Color.White
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Обновить")
        }
        LazyColumn {
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    state = postStates[post.id] ?: PostState.Loading
                )
            }
        }
    }
}




