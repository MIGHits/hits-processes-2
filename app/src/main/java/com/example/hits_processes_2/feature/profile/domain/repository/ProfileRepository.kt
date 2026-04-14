package com.example.hits_processes_2.feature.profile.domain.repository

import com.example.hits_processes_2.feature.profile.domain.model.ProfileUser

interface ProfileRepository {
    suspend fun getMyProfile(): Result<ProfileUser>
}
