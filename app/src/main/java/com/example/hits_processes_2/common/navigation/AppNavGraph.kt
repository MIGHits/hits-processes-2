package com.example.hits_processes_2.common.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hits_processes_2.feature.authorization.presentation.AuthorizationScreen
import com.example.hits_processes_2.feature.captain_selection.presentation.CaptainSelectionRoute
import com.example.hits_processes_2.feature.course_detail.presentation.CourseDetailsRoot
import com.example.hits_processes_2.feature.courses.presentation.CoursesRoot
import com.example.hits_processes_2.feature.draft.presentation.DraftRoute
import com.example.hits_processes_2.feature.profile.presentation.ProfileRoot
import com.example.hits_processes_2.feature.task_detail.presentation.TaskDetailRoot
import com.example.hits_processes_2.feature.task_creation.presentation.TaskCreationScreen
import com.example.hits_processes_2.feature.teams.presentation.TeamsRoute

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
                onProfileClick = {
                    navController.navigate(ScreenRoute.Profile.route)
                },
                onLoggedOut = {
                    navController.navigate(ScreenRoute.Authorization.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(ScreenRoute.Profile.route) {
            ProfileRoot(
                onNavigateBack = { navController.popBackStack() },
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
            val taskCreated by backStackEntry.savedStateHandle
                .getStateFlow(TASK_CREATED_KEY, false)
                .collectAsState()
            CourseDetailsRoot(
                courseId = courseId,
                taskCreated = taskCreated,
                onNavigateBack = navController::navigateUp,
                onTaskClick = { taskId, role ->
                    navController.navigate(
                        ScreenRoute.TaskDetail.createRoute(
                            courseId = courseId,
                            taskId = taskId,
                            role = role.name,
                        ),
                    )
                },
                onCreateTask = {
                    navController.navigate(ScreenRoute.TaskCreation.createRoute(courseId))
                },
                onTaskCreatedHandled = {
                    backStackEntry.savedStateHandle[TASK_CREATED_KEY] = false
                },
            )
        }

        composable(
            route = ScreenRoute.TaskDetail.route,
            arguments = listOf(
                navArgument(ScreenRoute.TaskDetail.COURSE_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.TaskDetail.TASK_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.TaskDetail.ROLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "STUDENT"
                },
            ),
        ) { backStackEntry ->
            TaskDetailRoot(
                courseId = backStackEntry.arguments?.getString(ScreenRoute.TaskDetail.COURSE_ID_ARG).orEmpty(),
                taskId = backStackEntry.arguments?.getString(ScreenRoute.TaskDetail.TASK_ID_ARG).orEmpty(),
                userRoleName = backStackEntry.arguments?.getString(ScreenRoute.TaskDetail.ROLE_ARG).orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                onOpenTeams = { courseId, taskId, teamFormationType, userRoleName ->
                    navController.navigate(
                        ScreenRoute.Teams.createRoute(
                            courseId = courseId,
                            taskId = taskId,
                            role = userRoleName,
                            formation = teamFormationType,
                        ),
                    )
                },
                onOpenDraft = { courseId, taskId, draftId, userRoleName ->
                    navController.navigate(
                        ScreenRoute.Draft.createRoute(
                            courseId = courseId,
                            taskId = taskId,
                            draftId = draftId,
                            role = userRoleName,
                        ),
                    )
                },
                onOpenCaptainSelection = { courseId, taskId, draftId, userRoleName ->
                    navController.navigate(
                        ScreenRoute.CaptainSelection.createRoute(
                            courseId = courseId,
                            taskId = taskId,
                            draftId = draftId,
                            role = userRoleName,
                        ),
                    )
                },
            )
        }

        composable(
            route = ScreenRoute.Teams.route,
            arguments = listOf(
                navArgument(ScreenRoute.Teams.COURSE_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.Teams.TASK_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.Teams.ROLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "STUDENT"
                },
                navArgument(ScreenRoute.Teams.FORMATION_ARG) {
                    type = NavType.StringType
                    defaultValue = "FREE"
                },
            ),
        ) { backStackEntry ->
            TeamsRoute(
                courseId = backStackEntry.arguments?.getString(ScreenRoute.Teams.COURSE_ID_ARG).orEmpty(),
                taskId = backStackEntry.arguments?.getString(ScreenRoute.Teams.TASK_ID_ARG).orEmpty(),
                userRoleName = backStackEntry.arguments?.getString(ScreenRoute.Teams.ROLE_ARG).orEmpty(),
                teamFormationName = backStackEntry.arguments?.getString(ScreenRoute.Teams.FORMATION_ARG).orEmpty(),
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = ScreenRoute.Draft.route,
            arguments = listOf(
                navArgument(ScreenRoute.Draft.COURSE_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.Draft.TASK_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.Draft.DRAFT_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.Draft.ROLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "STUDENT"
                },
            ),
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString(ScreenRoute.Draft.COURSE_ID_ARG).orEmpty()
            val taskId = backStackEntry.arguments?.getString(ScreenRoute.Draft.TASK_ID_ARG).orEmpty()
            val role = backStackEntry.arguments?.getString(ScreenRoute.Draft.ROLE_ARG).orEmpty()
            DraftRoute(
                courseId = courseId,
                taskId = taskId,
                draftId = backStackEntry.arguments?.getString(ScreenRoute.Draft.DRAFT_ID_ARG).orEmpty(),
                userRoleName = role,
                currentUserId = null,
                onNavigateBack = { navController.popBackStack() },
                onOpenTeams = { teamsCourseId, teamsTaskId, teamsRole ->
                    navController.navigate(
                        ScreenRoute.Teams.createRoute(
                            courseId = teamsCourseId,
                            taskId = teamsTaskId,
                            role = teamsRole,
                            formation = "DRAFT",
                        ),
                    ) {
                        popUpTo(ScreenRoute.Draft.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = ScreenRoute.CaptainSelection.route,
            arguments = listOf(
                navArgument(ScreenRoute.CaptainSelection.COURSE_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.CaptainSelection.TASK_ID_ARG) { type = NavType.StringType },
                navArgument(ScreenRoute.CaptainSelection.DRAFT_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(ScreenRoute.CaptainSelection.ROLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "TEACHER"
                },
            ),
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString(ScreenRoute.CaptainSelection.COURSE_ID_ARG).orEmpty()
            val taskId = backStackEntry.arguments?.getString(ScreenRoute.CaptainSelection.TASK_ID_ARG).orEmpty()
            val draftId = backStackEntry.arguments?.getString(ScreenRoute.CaptainSelection.DRAFT_ID_ARG)
            val role = backStackEntry.arguments?.getString(ScreenRoute.CaptainSelection.ROLE_ARG).orEmpty()
            CaptainSelectionRoute(
                courseId = courseId,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onCaptainsSelected = {
                    if (draftId.isNullOrBlank()) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(
                            ScreenRoute.Draft.createRoute(
                                courseId = courseId,
                                taskId = taskId,
                                draftId = draftId,
                                role = role,
                            ),
                        ) {
                            popUpTo(ScreenRoute.CaptainSelection.route) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(
            route = ScreenRoute.TaskCreation.destinationRoute,
            arguments = listOf(
                navArgument(ScreenRoute.TaskCreation.COURSE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            TaskCreationScreen(
                courseId = backStackEntry.arguments?.getString(ScreenRoute.TaskCreation.COURSE_ID_ARG),
                onNavigateBack = { navController.popBackStack() },
                onTaskCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(TASK_CREATED_KEY, true)
                    navController.popBackStack()
                },
            )
        }
    }
}

private const val TASK_CREATED_KEY = "taskCreated"
