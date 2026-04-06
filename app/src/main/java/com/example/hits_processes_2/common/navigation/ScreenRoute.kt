package com.example.hits_processes_2.common.navigation

sealed class ScreenRoute(val route: String) {
    data object Authorization : ScreenRoute("authorization")
    data object Courses : ScreenRoute("courses")
}
