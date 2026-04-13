package com.example.hits_processes_2.feature.task_edit.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskEditRequestDto(
    @SerialName("title") val title: String,
    @SerialName("text") val text: String,
    @SerialName("maxScore") val maxScore: Int,
    @SerialName("deadlineTime") val deadlineTime: String,
    @SerialName("fileIds") val fileIds: List<String>,
)
