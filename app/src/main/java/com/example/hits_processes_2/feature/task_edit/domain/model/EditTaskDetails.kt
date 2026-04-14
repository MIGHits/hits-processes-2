package com.example.hits_processes_2.feature.task_edit.domain.model

data class EditTaskDetails(
    val id: String,
    val title: String,
    val text: String,
    val deadlineIso: String?,
    val createdAtIso: String?,
    val updatedAtIso: String?,
    val maxScore: Int,
    val author: EditTaskAuthor?,
    val files: List<EditTaskExistingFile>,
)
