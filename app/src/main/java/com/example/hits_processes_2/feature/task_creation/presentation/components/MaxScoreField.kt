package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.hits_processes_2.R

@Composable
fun MaxScoreField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    LabeledTextField(
        label = stringResource(R.string.task_creation_max_score_label),
        placeholder = stringResource(R.string.task_creation_max_score_placeholder),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
