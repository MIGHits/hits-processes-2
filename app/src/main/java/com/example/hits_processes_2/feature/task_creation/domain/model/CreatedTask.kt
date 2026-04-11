package com.example.hits_processes_2.feature.task_creation.domain.model

data class CreatedTask(
    val id: String,
    val title: String,
    val text: String,
    val deadlineIso: String?,
    val maxScore: Int,
    val teamFormationType: TeamFormationType,
)
