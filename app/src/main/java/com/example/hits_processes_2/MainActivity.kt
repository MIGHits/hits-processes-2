package com.example.hits_processes_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.hits_processes_2.common.navigation.AppNavGraph
import com.example.hits_processes_2.common.navigation.ScreenRoute
import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.authorization.domain.SessionExpiredNotifier
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val tokenStorage: TokenStorage by inject()
    private val sessionExpiredNotifier: SessionExpiredNotifier by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = if (tokenStorage.getTokens() != null) {
            ScreenRoute.Courses.route
        } else {
            ScreenRoute.Authorization.route
        }

        setContent {
            Hitsprocesses2Theme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    sessionExpiredNotifier.sessionExpiredEvents.collect {
                        navController.navigate(ScreenRoute.Authorization.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                )
            }
        }
    }
}
