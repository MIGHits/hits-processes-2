package com.example.hits_processes_2.common.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.models.UserRole

/**
 * Course card component
 */
@Composable
fun CourseCard(
	courseName: String,
	courseDescription: String,
	userRole: UserRole,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Top
			) {
				Text(
					text = courseName,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.weight(1f),
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)
				Spacer(modifier = Modifier.width(8.dp))
				RoleBadge(role = userRole)
			}
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = courseDescription,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)
		}
	}
}

/**
 * Assignment card component
 */
@Composable
fun AssignmentCard(
	assignmentText: String,
	deadline: String,
	author: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Text(
				text = assignmentText,
				style = MaterialTheme.typography.titleSmall,
				maxLines = 3,
				overflow = TextOverflow.Ellipsis
			)
			Spacer(modifier = Modifier.height(12.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = "Дедлайн: $deadline",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				Text(
					text = author,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}

/**
 * Team card component
 */
@Composable
fun TeamCard(
	teamNumber: Int,
	modifier: Modifier = Modifier,
	content: @Composable ColumnScope.() -> Unit
) {
	Card(
		modifier = modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Text(
				text = "Команда $teamNumber",
				style = MaterialTheme.typography.titleMedium
			)
			Spacer(modifier = Modifier.height(12.dp))
			content()
		}
	}
}

/**
 * Info card for displaying labeled information
 */
@Composable
fun InfoCard(
	modifier: Modifier = Modifier,
	content: @Composable ColumnScope.() -> Unit
) {
	Card(
		modifier = modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			content()
		}
	}
}
