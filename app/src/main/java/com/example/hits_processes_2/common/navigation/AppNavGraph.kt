package com.example.hits_processes_2.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hits_processes_2.feature.authorization.presentation.AuthorizationScreen
import com.example.hits_processes_2.feature.course_detail.presentation.CourseDetailsRoot
import com.example.hits_processes_2.feature.courses.presentation.CoursesRoot
import com.example.hits_processes_2.feature.home.presentation.HomeScreen
import com.example.hits_processes_2.feature.task_creation.presentation.TaskCreationScreen

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
                    navController.navigate(ScreenRoute.Courses.route) {
                        popUpTo(ScreenRoute.Authorization.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(ScreenRoute.Courses.route) {
            CoursesRoot(
                onCourseClick = { courseId ->
                    navController.navigate(ScreenRoute.CourseDetails.createRoute(courseId))
                },
                onLoggedOut = {
                    navController.navigate(ScreenRoute.Authorization.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = ScreenRoute.CourseDetails.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId").orEmpty()
            CourseDetailsRoot(
                courseId = courseId,
                onNavigateBack = navController::navigateUp,
            )
        }

        composable(ScreenRoute.TaskCreation.route) {
            TaskCreationScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
