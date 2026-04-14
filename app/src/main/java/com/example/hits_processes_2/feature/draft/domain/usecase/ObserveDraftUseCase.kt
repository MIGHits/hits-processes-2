package com.example.hits_processes_2.feature.draft.domain.usecase

import com.example.hits_processes_2.feature.draft.domain.repository.DraftRepository

class ObserveDraftUseCase(
    private val repository: DraftRepository,
) {
    operator fun invoke(draftId: String) = repository.observeDraft(draftId)
}
