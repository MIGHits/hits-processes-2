package com.example.hits_processes_2.feature.task_edit.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.ui.component.FormPrimaryButton
import com.example.hits_processes_2.common.ui.component.FormSecondaryButton
import com.example.hits_processes_2.feature.task_edit.presentation.TaskEditUiEvent
import com.example.hits_processes_2.feature.task_edit.presentation.TaskEditUiState

@Composable
fun TaskEditContent(
    state: TaskEditUiState,
    paddingValues: PaddingValues,
    onEvent: (TaskEditUiEvent) -> Unit,
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val errorMessage = state.errorMessage
    if (errorMessage != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = { onEvent(TaskEditUiEvent.RetryClicked) },
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(text = stringResource(R.string.task_edit_retry_button))
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TaskEditLabeledTextField(
                    label = stringResource(R.string.task_edit_title_label),
                    value = state.title,
                    onValueChange = { onEvent(TaskEditUiEvent.TitleChanged(it)) },
                    placeholder = stringResource(R.string.task_edit_title_placeholder),
                    singleLine = true,
                )

                TaskEditLabeledTextField(
                    label = stringResource(R.string.task_edit_text_label),
                    value = state.text,
                    onValueChange = { onEvent(TaskEditUiEvent.TextChanged(it)) },
                    placeholder = stringResource(R.string.task_edit_text_placeholder),
                    minLines = 5,
                )

                TaskEditLabeledTextField(
                    label = stringResource(R.string.task_edit_max_score_label),
                    value = state.maxScore,
                    onValueChange = { onEvent(TaskEditUiEvent.MaxScoreChanged(it)) },
                    placeholder = stringResource(R.string.task_edit_max_score_placeholder),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                Column {
                    Text(
                        text = stringResource(R.string.task_edit_deadline_label),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    TaskEditDeadlineField(
                        deadlineMillis = state.deadlineMillis,
                        minimumDeadlineMillis = maxOf(
                            System.currentTimeMillis(),
                            state.initialDeadlineMillis ?: Long.MIN_VALUE,
                        ).takeIf { it != Long.MIN_VALUE },
                        onDeadlineSelected = { onEvent(TaskEditUiEvent.DeadlineChanged(it)) },
                        onInvalidDeadlineSelected = {
                            onEvent(TaskEditUiEvent.InvalidDeadlineSelected)
                        },
                    )
                }

                TaskEditFilesSection(
                    existingFiles = state.existingFiles,
                    newFiles = state.newFiles,
                    onExistingFileRemoved = { onEvent(TaskEditUiEvent.ExistingFileRemoved(it)) },
                    onNewFileRemoved = { onEvent(TaskEditUiEvent.NewFileRemoved(it)) },
                    onNewFilesSelected = { onEvent(TaskEditUiEvent.NewFilesSelected(it)) },
                )

                HorizontalDivider()

                FormPrimaryButton(
                    text = stringResource(R.string.task_edit_save_button),
                    onClick = { onEvent(TaskEditUiEvent.SaveClicked) },
                    isLoading = state.isSaving,
                )

                FormSecondaryButton(
                    text = stringResource(R.string.task_edit_cancel_button),
                    onClick = { onEvent(TaskEditUiEvent.CancelClicked) },
                    enabled = !state.isSaving,
                )
            }
        }
    }
}
