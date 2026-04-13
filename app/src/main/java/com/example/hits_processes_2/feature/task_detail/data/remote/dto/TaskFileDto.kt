package com.example.hits_processes_2.feature.task_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskFileDto(
    @SerialName("id") val id: String,
    @SerialName("fileName") val fileName: String? = null,
)
