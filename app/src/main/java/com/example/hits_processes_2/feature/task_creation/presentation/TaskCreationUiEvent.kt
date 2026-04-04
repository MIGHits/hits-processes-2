package com.example.hits_processes_2.feature.task_creation.presentation

sealed interface TaskCreationUiEvent {
    data class TaskTextChanged(val text: String) : TaskCreationUiEvent
    data class DeadlineSelected(val millis: Long) : TaskCreationUiEvent
    data class FilesSelected(val files: List<AttachedFile>) : TaskCreationUiEvent
    data class FileRemoved(val index: Int) : TaskCreationUiEvent
    data class TeamFormationRuleSelected(val rule: TeamFormationRule) : TaskCreationUiEvent
    data class TeamCountChanged(val count: Int) : TaskCreationUiEvent
    data class SubmissionStrategySelected(val strategy: SubmissionStrategy) : TaskCreationUiEvent
    data object TeamFormationDropdownToggled : TaskCreationUiEvent
    data object SubmissionStrategyDropdownToggled : TaskCreationUiEvent
    data object CreateTaskClicked : TaskCreationUiEvent
    data object BackClicked : TaskCreationUiEvent
}
