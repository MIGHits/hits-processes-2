package com.example.hits_processes_2.feature.task_edit.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskEditDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("text") val text: String,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("author") val author: TaskEditAuthorDto? = null,
    @SerialName("files") val files: List<TaskEditFileDto>? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("deadline") val deadline: String? = null,
    @SerialName("maxScore") val maxScore: Int = 0,
)
