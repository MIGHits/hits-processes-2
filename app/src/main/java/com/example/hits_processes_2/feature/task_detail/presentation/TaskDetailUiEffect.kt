package com.example.hits_processes_2.feature.task_detail.presentation

sealed interface TaskDetailUiEffect {
    data object NavigateBack : TaskDetailUiEffect
    data class NavigateToEdit(val courseId: String, val taskId: String) : TaskDetailUiEffect
    data class ShowMessage(val message: String) : TaskDetailUiEffect
    data class StartFileDownload(val fileId: String) : TaskDetailUiEffect
    data class OpenTeams(
        val courseId: String,
        val taskId: String,
        val teamFormationType: String,
        val userRoleName: String,
    ) : TaskDetailUiEffect
    data class OpenDraft(
        val courseId: String,
        val taskId: String,
        val draftId: String,
        val userRoleName: String,
    ) : TaskDetailUiEffect
    data class OpenCaptainSelection(
        val courseId: String,
        val taskId: String,
        val draftId: String?,
        val userRoleName: String,
    ) : TaskDetailUiEffect
}
