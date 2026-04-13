package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.task_detail.presentation.TaskDetailUiEvent
import com.example.hits_processes_2.feature.task_detail.presentation.TaskDetailUiState

@Composable
fun TaskDetailContent(
    state: TaskDetailUiState,
    paddingValues: PaddingValues,
    onEvent: (TaskDetailUiEvent) -> Unit,
) {
    val task = state.task

    if (task == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = state.errorMessage ?: stringResource(R.string.task_detail_error_load),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TaskDetailInfoCard(
            task = task,
            onFileClick = { onEvent(TaskDetailUiEvent.FileClicked(it)) },
        )

        if (state.userRole == CourseDetailsRole.STUDENT) {
            StudentSubmissionSection(
                files = state.submissionFiles,
                onFilesChanged = { onEvent(TaskDetailUiEvent.SubmissionFilesChanged(it)) },
                onFileRemoved = { onEvent(TaskDetailUiEvent.SubmissionFileRemoved(it)) },
                onSubmitClicked = { onEvent(TaskDetailUiEvent.SubmitClicked) },
                onCancelSubmissionClicked = { onEvent(TaskDetailUiEvent.CancelSubmissionClicked) },
            )
        } else {
            TeacherTaskActionsSection(
                onTeamsClicked = { onEvent(TaskDetailUiEvent.TeamsClicked) },
                onEditClicked = { onEvent(TaskDetailUiEvent.EditClicked) },
            )
        }
    }
}
