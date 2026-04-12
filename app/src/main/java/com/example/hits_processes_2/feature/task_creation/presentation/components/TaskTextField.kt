package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R

@Composable
fun TaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.task_creation_text_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text(text = stringResource(R.string.task_creation_text_placeholder)) },
            maxLines = 5,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            colors = taskCreationFieldColors(),
        )
    }
}
