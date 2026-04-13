package com.example.hits_processes_2.feature.task_detail.domain.model

data class TaskDetail(
    val id: String,
    val title: String,
    val text: String,
    val deadlineIso: String?,
    val createdAtIso: String?,
    val updatedAtIso: String?,
    val draftId: String?,
    val maxScore: Int,
    val teamFormationType: String,
    val author: TaskAuthor?,
    val files: List<TaskFile>,
)
