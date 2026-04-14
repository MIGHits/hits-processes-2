package com.example.hits_processes_2.feature.profile.data.repository

import com.example.hits_processes_2.common.network.ApiException
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.profile.data.remote.ProfileApi
import com.example.hits_processes_2.feature.profile.data.remote.toDomain
import com.example.hits_processes_2.feature.profile.domain.model.ProfileUser
import com.example.hits_processes_2.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val api: ProfileApi,
) : ProfileRepository {

    override suspend fun getMyProfile(): Result<ProfileUser> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = api::getMyProfile,
            converter = { it.toDomain() },
        ).recoverCatching { throwable ->
            throw throwable.toProfileException()
        }
    }
}

class ProfileException(
    val code: Int,
    override val message: String,
) : Exception(message)

private fun Throwable.toProfileException(): ProfileException = when (this) {
    is ProfileException -> this
    is ApiException -> ProfileException(code = code, message = message)
    else -> ProfileException(code = -1, message = message ?: "Failed to load profile")
}
