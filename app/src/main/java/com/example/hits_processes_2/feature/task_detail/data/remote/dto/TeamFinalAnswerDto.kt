package com.example.hits_processes_2.feature.task_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamFinalAnswerDto(
    @SerialName("id") val id: String,
    @SerialName("submittedAt") val submittedAt: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("taskAnswer") val taskAnswer: TaskAnswerDto? = null,
)

