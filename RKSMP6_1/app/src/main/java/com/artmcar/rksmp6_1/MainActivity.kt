package com.artmcar.rksmp6_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.artmcar.rksmp6_1.di.BaseApplication
import com.artmcar.rksmp6_1.presentation.BigPhotoScreen
import com.artmcar.rksmp6_1.presentation.ListScreen
import com.artmcar.rksmp6_1.ui.theme.RKSMP6_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as BaseApplication).container
        setContent {
            RKSMP6_1Theme {
                Surface {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = "list") {
                        composable("list") {
                            ListScreen(
                                getPhotos = container.getPhotoUseCase,
                                onPhotoClick = { id -> nav.navigate("detail/$id") }
                            )
                        }
                        composable("detail/{id}") { entry ->
                            val id = entry.arguments?.getString("id") ?: return@composable
                            BigPhotoScreen(
                                photoId = id,
                                repo = container.photoRepository,
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

