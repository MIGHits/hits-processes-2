package com.example.hits_processes_2.feature.task_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskAnswerDto(
    @SerialName("id") val id: String,
    @SerialName("files") val files: List<TaskFileDto>? = null,
    @SerialName("user") val user: TaskAuthorDto? = null,
    @SerialName("finalDecision") val finalDecision: Boolean = false,
    @SerialName("votesCount") val votesCount: Int = 0,
    @SerialName("votedUserIds") val votedUserIds: List<String> = emptyList(),
    @SerialName("uploadedAt") val uploadedAt: String? = null,
)

