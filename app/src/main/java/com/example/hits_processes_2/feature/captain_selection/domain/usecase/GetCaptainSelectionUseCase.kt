package com.example.hits_processes_2.feature.captain_selection.domain.usecase

import com.example.hits_processes_2.feature.captain_selection.domain.repository.CaptainSelectionRepository

class GetCaptainSelectionUseCase(
    private val repository: CaptainSelectionRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        taskId: String,
    ) = repository.getCaptainSelection(courseId, taskId)
}
