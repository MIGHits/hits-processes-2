package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.file_attachment.presentation.AttachedFilesSection
import com.example.hits_processes_2.feature.task_creation.presentation.TaskCreationUiEvent
import com.example.hits_processes_2.feature.task_creation.presentation.TaskCreationUiState
import androidx.compose.foundation.layout.Column

@Composable
fun TaskCreationContent(
    state: TaskCreationUiState,
    onEvent: (TaskCreationUiEvent) -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TitleField(
            value = state.title,
            onValueChange = { onEvent(TaskCreationUiEvent.TitleChanged(it)) },
        )

        TaskTextField(
            value = state.taskText,
            onValueChange = { onEvent(TaskCreationUiEvent.TaskTextChanged(it)) },
        )

        MaxScoreField(
            value = state.maxScore,
            onValueChange = { onEvent(TaskCreationUiEvent.MaxScoreChanged(it)) },
        )

        DeadlineField(
            deadlineMillis = state.deadlineMillis,
            onDeadlineSelected = { onEvent(TaskCreationUiEvent.DeadlineSelected(it)) },
        )

        AttachedFilesSection(
            files = state.attachedFiles,
            onFilesSelected = { onEvent(TaskCreationUiEvent.FilesSelected(it)) },
            onFileRemoved = { onEvent(TaskCreationUiEvent.FileRemoved(it)) },
        )

        TeamFormationDropdown(
            selected = state.teamFormationRule,
            expanded = state.isTeamFormationDropdownExpanded,
            onToggle = { onEvent(TaskCreationUiEvent.TeamFormationDropdownToggled) },
            onSelect = { onEvent(TaskCreationUiEvent.TeamFormationRuleSelected(it)) },
        )

        FinalizationDropdown(
            selected = state.finalizationRule,
            expanded = state.isFinalizationDropdownExpanded,
            onToggle = { onEvent(TaskCreationUiEvent.FinalizationDropdownToggled) },
            onSelect = { onEvent(TaskCreationUiEvent.FinalizationRuleSelected(it)) },
        )

        TeamCountField(
            count = state.teamCount,
            onCountChange = { onEvent(TaskCreationUiEvent.TeamCountChanged(it)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        CreateTaskButton(
            isCreating = state.isCreating,
            onClick = { onEvent(TaskCreationUiEvent.CreateTaskClicked) },
        )
    }
}
