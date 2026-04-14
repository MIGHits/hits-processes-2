package com.example.hits_processes_2.common.navigation

sealed class ScreenRoute(val route: String) {
    data object Authorization : ScreenRoute("authorization")
    data object Courses : ScreenRoute("courses")
    data object CourseDetails : ScreenRoute("course/{courseId}") {
        fun createRoute(courseId: String): String = "course/$courseId"
    }
    data object TaskDetail : ScreenRoute("task_detail/{courseId}/{taskId}?role={role}") {
        const val COURSE_ID_ARG = "courseId"
        const val TASK_ID_ARG = "taskId"
        const val ROLE_ARG = "role"

        fun createRoute(
            courseId: String,
            taskId: String,
            role: String,
        ): String = "task_detail/$courseId/$taskId?role=$role"
    }
    data object TaskEdit : ScreenRoute("task_edit/{courseId}/{taskId}") {
        const val COURSE_ID_ARG = "courseId"
        const val TASK_ID_ARG = "taskId"

        fun createRoute(
            courseId: String,
            taskId: String,
        ): String = "task_edit/$courseId/$taskId"
    }
    data object Home : ScreenRoute("home")
    data object Profile : ScreenRoute("profile")
    data object TaskCreation : ScreenRoute("task_creation") {
        const val COURSE_ID_ARG = "courseId"
        val destinationRoute = "$route?$COURSE_ID_ARG={$COURSE_ID_ARG}"

        fun createRoute(courseId: String): String = "$route?$COURSE_ID_ARG=$courseId"
    }
}
