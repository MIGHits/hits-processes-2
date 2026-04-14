package com.example.hits_processes_2.feature.task_edit.presentation

sealed interface TaskEditUiEffect {
    data object NavigateBack : TaskEditUiEffect
    data class ShowMessage(val message: String) : TaskEditUiEffect
}
