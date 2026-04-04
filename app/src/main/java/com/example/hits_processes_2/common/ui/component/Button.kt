package com.example.hits_processes_2.common.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Primary button with optional icon
 */
@Composable
fun PrimaryButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	icon: ImageVector? = null,
	enabled: Boolean = true
) {
	Button(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled
	) {
		if (icon != null) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				modifier = Modifier.size(20.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
		}
		Text(text = text)
	}
}

/**
 * Secondary button with optional icon
 */
@Composable
fun SecondaryButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	icon: ImageVector? = null,
	enabled: Boolean = true
) {
	OutlinedButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled
	) {
		if (icon != null) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				modifier = Modifier.size(20.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
		}
		Text(text = text)
	}
}

/**
 * Text button with optional icon
 */
@Composable
fun TextActionButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	icon: ImageVector? = null,
	enabled: Boolean = true
) {
	TextButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled
	) {
		if (icon != null) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				modifier = Modifier.size(20.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
		}
		Text(text = text)
	}
}

/**
 * Floating action button for main actions
 */
@Composable
fun MainFloatingActionButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	icon: ImageVector = Icons.Default.Add
) {
	FloatingActionButton(
		onClick = onClick,
		modifier = modifier
	) {
		Icon(
			imageVector = icon,
			contentDescription = "Добавить"
		)
	}
}