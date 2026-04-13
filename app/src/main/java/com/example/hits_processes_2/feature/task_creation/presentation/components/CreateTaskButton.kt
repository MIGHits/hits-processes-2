package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.ui.component.FormPrimaryButton

@Composable
fun CreateTaskButton(
    isCreating: Boolean,
    onClick: () -> Unit,
) {
    FormPrimaryButton(
        text = stringResource(R.string.task_creation_create_button),
        onClick = onClick,
        isLoading = isCreating,
    )
}
