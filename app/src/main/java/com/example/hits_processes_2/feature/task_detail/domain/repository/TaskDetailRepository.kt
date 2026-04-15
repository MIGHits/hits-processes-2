package com.example.hits_processes_2.feature.task_detail.domain.repository

import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.task_detail.domain.model.MyTeam
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskAnswer
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import com.example.hits_processes_2.feature.task_detail.domain.model.TeamFinalAnswer

interface TaskDetailRepository {

    suspend fun getTaskDetail(
        courseId: String,
        taskId: String,
    ): Result<TaskDetail>

    suspend fun getMyTeam(
        courseId: String,
        taskId: String,
    ): Result<MyTeam>

    suspend fun attachAnswer(
        taskId: String,
        files: List<UploadedFileAttachment>,
    ): Result<AttachAnswerResult>

    suspend fun getAllUserTaskAnswers(taskId: String): Result<List<TaskAnswer>>

    suspend fun getAllTeamTaskAnswers(taskId: String, teamId: String): Result<List<TaskAnswer>>

    suspend fun getAllUserVotedTaskAnswers(taskId: String): Result<List<TaskAnswer>>

    suspend fun voteForAnswer(taskId: String, answerId: String): Result<TeamFinalAnswer>

    suspend fun selectAnswer(taskId: String, answerId: String): Result<Unit>

    suspend fun getTeamFinalAnswer(taskId: String, teamId: String): Result<TeamFinalAnswer?>

    suspend fun unattachAnswer(taskId: String, taskAnswerId: String): Result<TeamFinalAnswer?>

    suspend fun submitAnswer(taskId: String): Result<Unit>

    suspend fun unsubmitAnswer(taskId: String): Result<Unit>
}

data class AttachAnswerResult(
    val newTaskAnswerId: String,
    val teamFinalAnswer: TeamFinalAnswer?,
)
