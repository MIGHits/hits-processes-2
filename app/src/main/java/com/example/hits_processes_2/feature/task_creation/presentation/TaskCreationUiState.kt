package com.example.hits_processes_2.feature.task_creation.presentation

data class TaskCreationUiState(
    val taskText: String = "",
    val deadlineMillis: Long? = null,
    val attachedFiles: List<AttachedFile> = emptyList(),
    val teamFormationRule: TeamFormationRule? = null,
    val teamCount: Int = 1,
    val submissionStrategy: SubmissionStrategy? = null,
    val isTeamFormationDropdownExpanded: Boolean = false,
    val isSubmissionStrategyDropdownExpanded: Boolean = false,
    val isCreating: Boolean = false,
)
