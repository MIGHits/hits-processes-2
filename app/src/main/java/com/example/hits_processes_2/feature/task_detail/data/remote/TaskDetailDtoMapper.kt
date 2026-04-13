package com.example.hits_processes_2.feature.task_detail.data.remote

import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskAuthorDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskDetailDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskFileDto
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskAuthor
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskFile

fun TaskDetailDto.toDomain(): TaskDetail = TaskDetail(
    id = id,
    title = title,
    text = text,
    deadlineIso = deadline,
    createdAtIso = createdAt,
    updatedAtIso = updatedAt,
    draftId = draftId,
    maxScore = maxScore,
    teamFormationType = teamFormationType.orEmpty(),
    author = author?.toDomain(),
    files = files.orEmpty().map(TaskFileDto::toDomain),
)

private fun TaskAuthorDto.toDomain(): TaskAuthor = TaskAuthor(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
)

private fun TaskFileDto.toDomain(): TaskFile = TaskFile(
    id = id,
    fileName = fileName,
)
