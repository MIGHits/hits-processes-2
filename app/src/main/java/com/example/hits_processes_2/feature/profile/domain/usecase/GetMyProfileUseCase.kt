package com.example.hits_processes_2.feature.profile.domain.usecase

import com.example.hits_processes_2.feature.profile.domain.repository.ProfileRepository

class GetMyProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke() = repository.getMyProfile()
}
