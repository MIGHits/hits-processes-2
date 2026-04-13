package com.example.hits_processes_2.feature.task_detail.presentation

sealed interface TaskDetailUiEffect {
    data object NavigateBack : TaskDetailUiEffect
    data class NavigateToEdit(val courseId: String, val taskId: String) : TaskDetailUiEffect
    data class ShowMessage(val message: String) : TaskDetailUiEffect
    data class StartFileDownload(val fileId: String) : TaskDetailUiEffect
}
