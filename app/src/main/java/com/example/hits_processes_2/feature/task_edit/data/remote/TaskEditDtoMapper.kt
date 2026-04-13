package com.example.hits_processes_2.feature.task_edit.data.remote

import com.example.hits_processes_2.feature.task_edit.data.remote.dto.TaskEditAuthorDto
import com.example.hits_processes_2.feature.task_edit.data.remote.dto.TaskEditDetailsDto
import com.example.hits_processes_2.feature.task_edit.data.remote.dto.TaskEditFileDto
import com.example.hits_processes_2.feature.task_edit.data.remote.dto.TaskEditRequestDto
import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskAuthor
import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskDetails
import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskExistingFile
import com.example.hits_processes_2.feature.task_edit.domain.model.UpdateTaskData

fun TaskEditDetailsDto.toDomain(): EditTaskDetails = EditTaskDetails(
    id = id,
    title = title,
    text = text,
    deadlineIso = deadline,
    createdAtIso = createdAt,
    updatedAtIso = updatedAt,
    maxScore = maxScore,
    author = author?.toDomain(),
    files = files.orEmpty().map(TaskEditFileDto::toDomain),
)

fun UpdateTaskData.toDto(): TaskEditRequestDto = TaskEditRequestDto(
    title = title,
    text = text,
    maxScore = maxScore,
    deadlineTime = deadlineTimeIso,
    fileIds = fileIds,
)

private fun TaskEditAuthorDto.toDomain(): EditTaskAuthor = EditTaskAuthor(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
)

private fun TaskEditFileDto.toDomain(): EditTaskExistingFile = EditTaskExistingFile(
    id = id,
    fileName = fileName,
)
