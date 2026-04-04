package com.example.hits_processes_2.common.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Standard text input field
 */
@Composable
fun InputField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	modifier: Modifier = Modifier,
	placeholder: String = "",
	enabled: Boolean = true,
	isError: Boolean = false,
	supportingText: String? = null
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		placeholder = { Text(placeholder) },
		modifier = modifier.fillMaxWidth(),
		enabled = enabled,
		isError = isError,
		supportingText = if (supportingText != null) {
			{ Text(supportingText) }
		} else null,
		singleLine = true
	)
}

/**
 * Password input field
 */
@Composable
fun PasswordInputField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	isError: Boolean = false,
	supportingText: String? = null
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		modifier = modifier.fillMaxWidth(),
		enabled = enabled,
		isError = isError,
		supportingText = if (supportingText != null) {
			{ Text(supportingText) }
		} else null,
		visualTransformation = PasswordVisualTransformation(),
		singleLine = true
	)
}

/**
 * Multi-line text input field
 */
@Composable
fun MultiLineInputField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	modifier: Modifier = Modifier,
	placeholder: String = "",
	minLines: Int = 4,
	enabled: Boolean = true,
	isError: Boolean = false,
	supportingText: String? = null
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		placeholder = { Text(placeholder) },
		modifier = modifier.fillMaxWidth(),
		enabled = enabled,
		isError = isError,
		supportingText = if (supportingText != null) {
			{ Text(supportingText) }
		} else null,
		minLines = minLines,
		maxLines = minLines + 2
	)
}

/**
 * Labeled text display (read-only)
 */
@Composable
fun LabeledText(
	label: String,
	text: String,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(
			text = label,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = text,
			style = MaterialTheme.typography.bodyLarge
		)
	}
}
