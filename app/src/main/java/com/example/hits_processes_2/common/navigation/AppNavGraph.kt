package com.example.hits_processes_2.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hits_processes_2.feature.authorization.presentation.AuthorizationScreen
import com.example.hits_processes_2.feature.home.presentation.HomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(ScreenRoute.Authorization.route) {
            AuthorizationScreen(
                onAuthSuccess = {
                    navController.navigate(ScreenRoute.Home.route) {
                        popUpTo(ScreenRoute.Authorization.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(ScreenRoute.Home.route) {
            HomeScreen(
                onLoggedOut = {
                    navController.navigate(ScreenRoute.Authorization.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
