package com.example.hits_processes_2.feature.task_creation.domain.model

data class CreateTaskData(
    val title: String,
    val text: String,
    val maxScore: Int,
    val deadlineTimeIso: String,
    val teamFormationType: TeamFormationType,
    val teamsAmount: Int,
    val fileIds: List<String>,
)
