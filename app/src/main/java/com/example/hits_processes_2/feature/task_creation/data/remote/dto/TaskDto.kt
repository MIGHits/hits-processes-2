package com.example.hits_processes_2.feature.task_creation.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("text") val text: String,
    @SerialName("deadline") val deadline: String? = null,
    @SerialName("maxScore") val maxScore: Int = 0,
    @SerialName("teamFormationType") val teamFormationType: String? = null,
)
