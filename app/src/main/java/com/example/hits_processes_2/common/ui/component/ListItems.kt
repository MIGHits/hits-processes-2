package com.example.hits_processes_2.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * User avatar with initials
 */
@Composable
fun UserAvatar(
	initials: String,
	modifier: Modifier = Modifier,
	size: Dp = 48.dp
) {
	Box(
		modifier = modifier
			.size(size)
			.clip(CircleShape)
			.background(MaterialTheme.colorScheme.primary),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = initials,
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onPrimary,
			textAlign = TextAlign.Center
		)
	}
}

/**
 * File item display
 */
@Composable
fun FileItem(
	fileName: String,
	modifier: Modifier = Modifier,
	onDelete: (() -> Unit)? = null
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surfaceVariant,
		shape = MaterialTheme.shapes.small
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					imageVector = Icons.Default.Description,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = fileName,
					style = MaterialTheme.typography.bodyMedium
				)
			}
			if (onDelete != null) {
				IconButton(onClick = onDelete) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = "Удалить",
						tint = MaterialTheme.colorScheme.error
					)
				}
			}
		}
	}
}

/**
 * Team member item
 */
@Composable
fun TeamMemberItem(
	memberName: String,
	isCaptain: Boolean,
	modifier: Modifier = Modifier,
	actions: @Composable RowScope.() -> Unit = {}
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surfaceVariant,
		shape = MaterialTheme.shapes.small
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = memberName,
					style = MaterialTheme.typography.bodyMedium
				)
				if (isCaptain) {
					TextBadge(text = "Капитан")
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				actions()
			}
		}
	}
}

/**
 * Participant item with email
 */
@Composable
fun ParticipantItem(
	firstName: String,
	lastName: String,
	email: String,
	isMain: Boolean = false,
	modifier: Modifier = Modifier,
	actions: @Composable RowScope.() -> Unit = {}
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(modifier = Modifier.weight(1f)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "$firstName $lastName",
					style = MaterialTheme.typography.titleSmall
				)
				if (isMain) {
					TextBadge(text = "Главный")
				}
			}
			Text(
				text = email,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			actions()
		}
	}
}

/**
 * Icon list item for action sheets
 */
@Composable
fun ActionSheetItem(
	text: String,
	icon: ImageVector,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	tint: Color = MaterialTheme.colorScheme.onSurface
) {
	TextButton(
		onClick = onClick,
		modifier = modifier.fillMaxWidth(),
		contentPadding = PaddingValues(16.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = tint,
				modifier = Modifier.size(24.dp)
			)
			Spacer(modifier = Modifier.width(12.dp))
			Text(
				text = text,
				style = MaterialTheme.typography.bodyLarge,
				color = tint
			)
		}
	}
}