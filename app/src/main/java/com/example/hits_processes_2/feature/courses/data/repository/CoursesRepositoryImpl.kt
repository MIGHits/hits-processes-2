package com.example.hits_processes_2.feature.courses.data.repository

import com.example.hits_processes_2.common.network.ApiException
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.courses.data.remote.CoursesApi
import com.example.hits_processes_2.feature.courses.data.remote.toDomain
import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseCreateRequestDto
import com.example.hits_processes_2.feature.courses.domain.model.CourseShort
import com.example.hits_processes_2.feature.courses.domain.model.UserProfile
import com.example.hits_processes_2.feature.courses.domain.repository.CoursesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoursesRepositoryImpl(
    private val api: CoursesApi,
) : CoursesRepository {

    override suspend fun getMyCourses(): Result<List<CourseShort>> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = api::getMyCourses,
            converter = { it.toDomain() },
        ).recoverCatching { throwable ->
            throw throwable.toCoursesException()
        }
    }

    override suspend fun createCourse(
        name: String,
        description: String,
    ): Result<CourseShort> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.createCourse(CourseCreateRequestDto(name = name, description = description)) },
            converter = { it.toDomain() },
        ).recoverCatching { throwable ->
            throw throwable.toCoursesException()
        }
    }

    override suspend fun joinCourse(
        code: String,
    ): Result<CourseShort> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.joinCourse(code) },
            converter = { it.toDomain() },
        ).recoverCatching { throwable ->
            throw throwable.toCoursesException()
        }
    }

    override suspend fun getMyProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = api::getMyProfile,
            converter = { it.toDomain() },
        ).recoverCatching { throwable ->
            throw throwable.toCoursesException()
        }
    }
}

class CoursesException(
    val code: Int,
    override val message: String,
) : Exception(message)

private fun Throwable.toCoursesException(): CoursesException {
    return when (this) {
        is CoursesException -> this
        is ApiException -> CoursesException(code = code, message = message)
        else -> CoursesException(code = -1, message = message ?: "Failed to load courses")
    }
}
