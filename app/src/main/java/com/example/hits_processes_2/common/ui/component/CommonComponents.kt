package com.example.hits_processes_2.common.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * File upload zone component
 */
@Composable
fun FileUploadZone(
	onFileSelect: () -> Unit,
	modifier: Modifier = Modifier
) {
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.border(
				width = 2.dp,
				color = MaterialTheme.colorScheme.outline,
				shape = MaterialTheme.shapes.medium
			),
		color = MaterialTheme.colorScheme.surface,
		shape = MaterialTheme.shapes.medium
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(32.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Icon(
				imageVector = Icons.Default.Upload,
				contentDescription = null,
				modifier = Modifier.size(48.dp),
				tint = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = "Загрузите файлы",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center
			)
			Spacer(modifier = Modifier.height(16.dp))
			SecondaryButton(
				text = "Выбрать файлы",
				onClick = onFileSelect
			)
		}
	}
}

/**
 * Empty state component
 */
@Composable
fun EmptyState(
	message: String,
	modifier: Modifier = Modifier,
	action: (@Composable () -> Unit)? = null
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = message,
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center
		)
		if (action != null) {
			Spacer(modifier = Modifier.height(16.dp))
			action()
		}
	}
}

/**
 * Loading indicator
 */
@Composable
fun LoadingIndicator(
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator()
	}
}

/**
 * Section header
 */
@Composable
fun SectionHeader(
	title: String,
	modifier: Modifier = Modifier
) {
	Text(
		text = title,
		style = MaterialTheme.typography.titleMedium,
		modifier = modifier.padding(vertical = 8.dp)
	)
}

/**
 * Divider with spacing
 */
@Composable
fun SpacedDivider(
	modifier: Modifier = Modifier
) {
	Spacer(modifier = Modifier.height(12.dp))
	HorizontalDivider(modifier = modifier)
	Spacer(modifier = Modifier.height(12.dp))
}