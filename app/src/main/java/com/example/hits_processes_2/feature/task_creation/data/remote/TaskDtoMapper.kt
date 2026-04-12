package com.example.hits_processes_2.feature.task_creation.data.remote

import com.example.hits_processes_2.feature.task_creation.data.remote.dto.TaskDto
import com.example.hits_processes_2.feature.task_creation.domain.model.CreatedTask
import com.example.hits_processes_2.feature.task_creation.domain.model.TeamFormationType

fun TaskDto.toDomain(): CreatedTask = CreatedTask(
    id = id,
    title = title,
    text = text,
    deadlineIso = deadline,
    maxScore = maxScore,
    teamFormationType = teamFormationType.toDomainTeamFormationType(),
)

private fun String?.toDomainTeamFormationType(): TeamFormationType {
    return when (this) {
        TeamFormationType.RANDOM.name -> TeamFormationType.RANDOM
        TeamFormationType.FREE.name -> TeamFormationType.FREE
        TeamFormationType.CUSTOM.name -> TeamFormationType.CUSTOM
        TeamFormationType.DRAFT.name -> TeamFormationType.DRAFT
        else -> TeamFormationType.RANDOM
    }
}
