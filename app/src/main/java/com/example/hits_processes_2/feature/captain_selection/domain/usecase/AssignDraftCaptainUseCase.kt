package com.example.hits_processes_2.feature.captain_selection.domain.usecase

import com.example.hits_processes_2.feature.captain_selection.domain.repository.CaptainSelectionRepository

class AssignDraftCaptainUseCase(
    private val repository: CaptainSelectionRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        taskId: String,
        studentId: String,
    ) = repository.assignCaptain(courseId, taskId, studentId)
}
