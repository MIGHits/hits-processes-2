package com.example.hits_processes_2.common.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Top app bar with back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomTopAppBar(
	title: String,
	onNavigateBack: () -> Unit,
	actions: @Composable RowScope.() -> Unit = {}
) {
	TopAppBar(
		title = {
			Text(
				text = title,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		},
		navigationIcon = {
			IconButton(onClick = onNavigateBack) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Назад"
				)
			}
		},
		actions = actions
	)
}

/**
 * Top app bar with user profile for courses screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesTopAppBar(
	userName: String,
	onProfileClick: () -> Unit
) {
	Surface(
		shadowElevation = 2.dp
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = "Курсы",
						style = MaterialTheme.typography.titleLarge
					)
					Text(
						text = userName,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				IconButton(onClick = onProfileClick) {
					Icon(
						imageVector = Icons.Default.Person,
						contentDescription = "Профиль",
						tint = MaterialTheme.colorScheme.onSurface
					)
				}
			}
		}
	}
}