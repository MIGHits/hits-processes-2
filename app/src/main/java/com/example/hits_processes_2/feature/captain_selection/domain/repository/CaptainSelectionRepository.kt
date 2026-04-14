package com.example.hits_processes_2.feature.captain_selection.domain.repository

import com.example.hits_processes_2.feature.captain_selection.domain.model.CaptainSelection

interface CaptainSelectionRepository {
    suspend fun getCaptainSelection(
        courseId: String,
        taskId: String,
    ): Result<CaptainSelection>

    suspend fun assignCaptain(
        courseId: String,
        taskId: String,
        studentId: String,
    ): Result<Unit>

    suspend fun removeCaptain(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ): Result<Unit>
}
