package com.example.hits_processes_2.feature.task_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDetailDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("text") val text: String,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("author") val author: TaskAuthorDto? = null,
    @SerialName("files") val files: List<TaskFileDto>? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("deadline") val deadline: String? = null,
    @SerialName("draftId") val draftId: String? = null,
    @SerialName("maxScore") val maxScore: Int = 0,
    @SerialName("teamFormationType") val teamFormationType: String? = null,
)
