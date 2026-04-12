package com.example.hits_processes_2.feature.task_creation.presentation

sealed interface TaskCreationUiEffect {
    data object NavigateBack : TaskCreationUiEffect
    data object TaskCreated : TaskCreationUiEffect
    data class ShowError(val message: String) : TaskCreationUiEffect
}
