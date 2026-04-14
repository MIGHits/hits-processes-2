package com.example.hits_processes_2.feature.task_edit.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskEditFileDto(
    @SerialName("id") val id: String,
    @SerialName("fileName") val fileName: String? = null,
)
