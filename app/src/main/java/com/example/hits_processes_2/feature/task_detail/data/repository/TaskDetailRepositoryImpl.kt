package com.example.hits_processes_2.feature.task_detail.data.repository

import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.task_detail.data.remote.TaskDetailApi
import com.example.hits_processes_2.feature.task_detail.data.remote.toDomain as toTaskAnswerDomain
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.FileAnswerDto
import com.example.hits_processes_2.feature.task_detail.data.remote.toDomain
import com.example.hits_processes_2.feature.task_detail.data.remote.toDomain as toTaskDetailDomain
import com.example.hits_processes_2.feature.task_detail.domain.model.MyTeam
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskAnswer
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import com.example.hits_processes_2.feature.task_detail.domain.model.TeamFinalAnswer
import com.example.hits_processes_2.feature.task_detail.domain.repository.AttachAnswerResult
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class TaskDetailRepositoryImpl(
    private val api: TaskDetailApi,
) : TaskDetailRepository {

    override suspend fun getTaskDetail(
        courseId: String,
        taskId: String,
    ): Result<TaskDetail> {
        return safeApiCall(
            apiCall = { api.getTask(courseId, taskId) },
            converter = { it.toTaskDetailDomain() },
        )
    }

    override suspend fun getMyTeam(
        courseId: String,
        taskId: String,
    ): Result<MyTeam> {
        return safeApiCall(
            apiCall = { api.getMyTeam(courseId, taskId) },
            converter = { dto -> MyTeam(id = dto.id, isCaptain = dto.isCaptain) },
        )
    }

    override suspend fun attachAnswer(
        taskId: String,
        files: List<UploadedFileAttachment>,
    ): Result<AttachAnswerResult> {
        val body = files.map { FileAnswerDto(id = it.id, fileName = it.fileName) }
        return runCatching {
            val response = api.attachAnswer(taskId, body)
            if (!response.isSuccessful) {
                throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = response.message().ifBlank { "Failed to attach answer" },
                )
            }
            val responseBody = response.body()
                ?: throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = "Empty response body",
                )
            AttachAnswerResult(
                newTaskAnswerId = responseBody.newTaskAnswerId.orEmpty(),
                teamFinalAnswer = responseBody.finalTaskAnswer?.toDomain(),
            )
        }
    }

    override suspend fun getAllUserTaskAnswers(taskId: String): Result<List<TaskAnswer>> {
        return runCatching {
            val response = api.getAllUserTaskAnswers(taskId)
            if (!response.isSuccessful) {
                throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = response.message().ifBlank { "Failed to load user answers" },
                )
            }
            val body = response.body()
                ?: throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = "Empty response body",
                )
            body.map { it.toTaskAnswerDomain() }
        }
    }

    override suspend fun getTeamFinalAnswer(taskId: String, teamId: String): Result<TeamFinalAnswer?> {
        return runCatching {
            val response = api.getTeamFinalAnswer(taskId, teamId)
            if (response.code() == 404) return@runCatching null
            if (!response.isSuccessful) {
                throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = response.message().ifBlank { "Failed to load final answer" },
                )
            }
            val body = response.body()
                ?: throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = "Empty response body",
                )
            body.toDomain()
        }
    }

    override suspend fun unattachAnswer(taskId: String, taskAnswerId: String): Result<TeamFinalAnswer?> {
        return runCatching {
            val response = api.unattachAnswer(taskId, taskAnswerId)
            if (!response.isSuccessful) {
                throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = response.message().ifBlank { "Failed to unattach answer" },
                )
            }
            response.body()?.toDomain()
        }
    }

    override suspend fun submitAnswer(taskId: String): Result<Unit> {
        return runCatching {
            val response = api.submitAnswer(taskId)
            if (!response.isSuccessful) {
                throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = response.message().ifBlank { "Failed to submit answer" },
                )
            }
        }
    }

    override suspend fun unsubmitAnswer(taskId: String): Result<Unit> {
        return runCatching {
            val response = api.unsubmitAnswer(taskId)
            if (!response.isSuccessful) {
                throw com.example.hits_processes_2.common.network.ApiException(
                    code = response.code(),
                    message = response.message().ifBlank { "Failed to unsubmit answer" },
                )
            }
        }
    }
}
