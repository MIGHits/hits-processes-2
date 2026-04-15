package com.example.hits_processes_2.feature.task_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyTeamDto(
    @SerialName("id") val id: String? = null,
    @SerialName("isCaptain") val isCaptain: Boolean = false,
)
