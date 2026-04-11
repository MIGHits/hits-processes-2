package com.example.hits_processes_2.common.navigation

sealed class ScreenRoute(val route: String) {
    data object Authorization : ScreenRoute("authorization")
    data object Courses : ScreenRoute("courses")
    data object CourseDetails : ScreenRoute("course/{courseId}") {
        fun createRoute(courseId: String): String = "course/$courseId"
    }
    data object Home : ScreenRoute("home")
    data object TaskCreation : ScreenRoute("task_creation") {
        const val COURSE_ID_ARG = "courseId"
        val destinationRoute = "$route?$COURSE_ID_ARG={$COURSE_ID_ARG}"

        fun createRoute(courseId: String): String = "$route?$COURSE_ID_ARG=$courseId"
    }
}
