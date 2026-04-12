package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R

@Composable
fun TitleField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    LabeledTextField(
        label = stringResource(R.string.task_creation_title_label),
        placeholder = stringResource(R.string.task_creation_title_placeholder),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
    )
}
