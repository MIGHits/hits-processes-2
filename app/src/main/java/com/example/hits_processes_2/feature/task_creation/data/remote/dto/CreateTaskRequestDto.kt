package com.example.hits_processes_2.feature.task_creation.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskRequestDto(
    @SerialName("title") val title: String,
    @SerialName("text") val text: String,
    @SerialName("maxScore") val maxScore: Int,
    @SerialName("deadlineTime") val deadlineTime: String,
    @SerialName("teamFormationType") val teamFormationType: String,
    @SerialName("teamsAmount") val teamsAmount: Int,
    @SerialName("fileIds") val fileIds: List<String>,
)
