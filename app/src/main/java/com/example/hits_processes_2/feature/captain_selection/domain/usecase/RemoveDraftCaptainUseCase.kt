package com.example.hits_processes_2.feature.captain_selection.domain.usecase

import com.example.hits_processes_2.feature.captain_selection.domain.repository.CaptainSelectionRepository

class RemoveDraftCaptainUseCase(
    private val repository: CaptainSelectionRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ) = repository.removeCaptain(courseId, taskId, teamId, studentId)
}
