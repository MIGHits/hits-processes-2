package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun taskCreationFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
