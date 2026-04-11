package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.hits_processes_2.R

@Composable
fun TeamCountField(
    count: Int,
    onCountChange: (Int) -> Unit,
) {
    LabeledTextField(
        label = stringResource(R.string.task_creation_team_count_label),
        placeholder = "",
        value = count.toString(),
        onValueChange = { input -> input.toIntOrNull()?.let(onCountChange) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
