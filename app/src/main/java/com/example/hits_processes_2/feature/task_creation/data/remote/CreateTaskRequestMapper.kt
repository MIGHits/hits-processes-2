package com.example.hits_processes_2.feature.task_creation.data.remote

import com.example.hits_processes_2.feature.task_creation.data.remote.dto.CreateTaskRequestDto
import com.example.hits_processes_2.feature.task_creation.domain.model.CreateTaskData

fun CreateTaskData.toDto(): CreateTaskRequestDto = CreateTaskRequestDto(
    title = title,
    text = text,
    maxScore = maxScore,
    deadlineTime = deadlineTimeIso,
    teamFormationType = teamFormationType.name,
    taskAnswerFinalizationType = taskAnswerFinalizationType.name,
    teamsAmount = teamsAmount,
    fileIds = fileIds,
)
