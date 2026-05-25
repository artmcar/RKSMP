package com.artmcar.rksmp6_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.artmcar.rksmp6_6.di.ApplicationContainer
import com.artmcar.rksmp6_6.di.BaseApplication
import com.artmcar.rksmp6_6.domain.Prize
import com.artmcar.rksmp6_6.presentation.LoginScreen
import com.artmcar.rksmp6_6.presentation.PrizeDetailScreen
import com.artmcar.rksmp6_6.presentation.PrizesListScreen
import com.artmcar.rksmp6_6.ui.theme.RKSMP6_6Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as BaseApplication).container
        val initialToken = runBlocking { container.tokenStore.tokenFlow.first() }
        val startDestination = if (initialToken.isNullOrBlank()) Routes.LOGIN else Routes.PRIZES
        setContent {
            RKSMP6_6Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NobelNavGraph(container = container, startDestination = startDestination)
                }
            }
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val PRIZES = "prizes"
    const val DETAIL = "detail"
}

@Composable
private fun NobelNavGraph(container: ApplicationContainer, startDestination: String) {
    val navController = rememberNavController()
    val selectedPrize = remember { mutableStateOf<Prize?>(null) }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                loginUseCase = container.loginUseCase,
                onLoggedIn = {
                    navController.navigate(Routes.PRIZES) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.PRIZES) {
            PrizesListScreen(
                getPrizes = container.getPrizesUseCase,
                getFavorites = container.getFavoritesUseCase,
                toggleFavorite = container.toggleFavoriteUseCase,
                logout = container.logoutUseCase,
                onPrizeClick = { prize ->
                    selectedPrize.value = prize
                    navController.navigate(Routes.DETAIL)
                },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRIZES) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.DETAIL) {
            val prize = selectedPrize.value
            if (prize == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                PrizeDetailScreen(prize = prize, onBack = { navController.popBackStack() })
            }
        }
    }
}
