package com.example.hits_processes_2.common.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.hits_processes_2.models.SubmissionStatus
import com.example.hits_processes_2.models.UserRole

/**
 * Badge component for user roles
 */
@Composable
fun RoleBadge(
	role: UserRole,
	modifier: Modifier = Modifier
) {
	val (text, containerColor) = when (role) {
		UserRole.TEACHER, UserRole.MAIN_TEACHER ->
			"Преподаватель" to MaterialTheme.colorScheme.primary
		UserRole.STUDENT ->
			"Студент" to MaterialTheme.colorScheme.secondary
	}

	Badge(
		modifier = modifier,
		containerColor = containerColor
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.labelSmall
		)
	}
}

/**
 * Badge component for submission status
 */
@Composable
fun SubmissionStatusBadge(
	status: SubmissionStatus,
	modifier: Modifier = Modifier
) {
	val (text, containerColor) = when (status) {
		SubmissionStatus.SUBMITTED ->
			"Сдано" to MaterialTheme.colorScheme.tertiary
		SubmissionStatus.LATE ->
			"Сдано с опозданием" to MaterialTheme.colorScheme.error
		SubmissionStatus.NOT_SUBMITTED ->
			"Не сдано" to MaterialTheme.colorScheme.error
	}

	Badge(
		modifier = modifier,
		containerColor = containerColor
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.labelSmall
		)
	}
}

/**
 * Simple text badge
 */
@Composable
fun TextBadge(
	text: String,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.primary
) {
	Badge(
		modifier = modifier,
		containerColor = containerColor
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.labelSmall
		)
	}
}