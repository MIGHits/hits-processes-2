package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabeledTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder) },
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            colors = taskCreationFieldColors(),
        )
    }
}
