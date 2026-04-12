package com.example.hits_processes_2.feature.task_creation.presentation

import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

data class TaskCreationUiState(
    val title: String = "",
    val taskText: String = "",
    val maxScore: String = "",
    val deadlineMillis: Long? = null,
    val attachedFiles: List<SelectedFileAttachment> = emptyList(),
    val teamFormationRule: TeamFormationRule? = null,
    val teamCount: String = "1",
    val isTeamFormationDropdownExpanded: Boolean = false,
    val isCreating: Boolean = false,
)
