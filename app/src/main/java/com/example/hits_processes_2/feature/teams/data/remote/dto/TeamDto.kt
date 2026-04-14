package com.example.hits_processes_2.feature.teams.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamShortListDto(
    @SerialName("teams") val teams: List<TeamShortDto> = emptyList(),
)

@Serializable
data class TeamShortDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String? = null,
)

@Serializable
data class TeamDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String? = null,
    @SerialName("captain") val captain: UserDto? = null,
    @SerialName("members") val members: List<UserDto> = emptyList(),
    @SerialName("submission") val submission: FileDto? = null,
    @SerialName("submittedAt") val submittedAt: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("grade") val grade: Int? = null,
)

@Serializable
data class FileDto(
    @SerialName("id") val id: String? = null,
    @SerialName("fileName") val fileName: String? = null,
)

@Serializable
data class FinalTaskAnswerDto(
    @SerialName("id") val id: String? = null,
    @SerialName("score") val score: Int? = null,
    @SerialName("submittedAt") val submittedAt: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("taskAnswer") val taskAnswer: TaskAnswerDto? = null,
)

@Serializable
data class TaskAnswerDto(
    @SerialName("id") val id: String? = null,
    @SerialName("files") val files: List<FileDto> = emptyList(),
)

@Serializable
data class TaskRateRequestDto(
    @SerialName("rate") val rate: Int,
)

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("email") val email: String? = null,
)

@Serializable
data class UserCourseListDto(
    @SerialName("userCourseList") val userCourseList: List<UserCourseDto> = emptyList(),
)

@Serializable
data class UserCourseDto(
    @SerialName("userModel") val userModel: UserDto? = null,
    @SerialName("userRole") val userRole: String? = null,
)
