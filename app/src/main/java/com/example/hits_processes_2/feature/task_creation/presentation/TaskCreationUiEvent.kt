package com.example.hits_processes_2.feature.task_creation.presentation

import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

sealed interface TaskCreationUiEvent {
    data class TitleChanged(val text: String) : TaskCreationUiEvent
    data class TaskTextChanged(val text: String) : TaskCreationUiEvent
    data class MaxScoreChanged(val value: String) : TaskCreationUiEvent
    data class DeadlineSelected(val millis: Long) : TaskCreationUiEvent
    data class FilesSelected(val files: List<SelectedFileAttachment>) : TaskCreationUiEvent
    data class FileRemoved(val index: Int) : TaskCreationUiEvent
    data class TeamFormationRuleSelected(val rule: TeamFormationRule) : TaskCreationUiEvent
    data class FinalizationRuleSelected(val rule: TaskAnswerFinalizationRule) : TaskCreationUiEvent
    data class TeamCountChanged(val count: String) : TaskCreationUiEvent
    data object TeamFormationDropdownToggled : TaskCreationUiEvent
    data object FinalizationDropdownToggled : TaskCreationUiEvent
    data object CreateTaskClicked : TaskCreationUiEvent
    data object BackClicked : TaskCreationUiEvent
}
