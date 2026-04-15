package com.example.hits_processes_2.feature.task_detail.data.remote.dto

import com.example.hits_processes_2.feature.teams.data.remote.dto.FinalTaskAnswerDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinalTaskAnswerWithAnswerIdDto(
    @SerialName("newTaskAnswerId") val newTaskAnswerId: String? = null,
    @SerialName("finalTaskAnswer") val finalTaskAnswer: FinalTaskAnswerDto? = null,
)

