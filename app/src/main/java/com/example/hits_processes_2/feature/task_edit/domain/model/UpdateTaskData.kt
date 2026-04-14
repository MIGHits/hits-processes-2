package com.example.hits_processes_2.feature.task_edit.domain.model

data class UpdateTaskData(
    val title: String,
    val text: String,
    val maxScore: Int,
    val deadlineTimeIso: String,
    val fileIds: List<String>,
)
