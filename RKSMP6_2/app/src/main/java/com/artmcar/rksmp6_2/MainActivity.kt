package com.artmcar.rksmp6_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.artmcar.rksmp6_2.di.BaseApplication
import com.artmcar.rksmp6_2.presentation.LaureatesListScreen
import com.artmcar.rksmp6_2.presentation.MoreInfoScreen
import com.artmcar.rksmp6_2.ui.theme.RKSMP6_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as BaseApplication).container
        setContent {
            RKSMP6_2Theme {
                Surface {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = "list") {
                        composable("list") {
                            LaureatesListScreen(
                                getLaureates = container.getLaureatesUseCase,
                                onItemClick = { id -> nav.navigate("detail/$id") }
                            )
                        }
                        composable("detail/{id}") { entry ->
                            val id = entry.arguments?.getString("id") ?: return@composable
                            MoreInfoScreen(
                                id = id,
                                useCase = container.getLaureateDetailUseCase,
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
