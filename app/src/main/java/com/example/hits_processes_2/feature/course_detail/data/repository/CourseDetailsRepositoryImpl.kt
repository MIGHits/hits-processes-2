package com.example.hits_processes_2.feature.course_detail.data.repository

import com.example.hits_processes_2.common.network.ApiException
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.common.network.safeApiCallUnit
import com.example.hits_processes_2.feature.course_detail.data.remote.CourseDetailsApi
import com.example.hits_processes_2.feature.course_detail.data.remote.toApiValue
import com.example.hits_processes_2.feature.course_detail.data.remote.toDomain
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseEditRequestDto
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetails
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseTask
import com.example.hits_processes_2.feature.course_detail.domain.repository.CourseDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseDetailsRepositoryImpl(
    private val api: CourseDetailsApi,
) : CourseDetailsRepository {

    override suspend fun getCourse(courseId: String): Result<CourseDetails> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.getCourse(courseId) },
            converter = { it.toDomain() },
        ).recoverCatching { throw it.toCourseDetailsException() }
    }

    override suspend fun getTasks(courseId: String): Result<List<CourseTask>> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.getTasks(courseId) },
            converter = { it.toDomain() },
        ).recoverCatching { throw it.toCourseDetailsException() }
    }

    override suspend fun getParticipants(courseId: String): Result<List<CourseParticipant>> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.getUsers(courseId) },
            converter = { it.toDomain() },
        ).recoverCatching { throw it.toCourseDetailsException() }
    }

    override suspend fun editCourse(
        courseId: String,
        name: String,
        description: String,
    ): Result<CourseDetails> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.editCourse(courseId, CourseEditRequestDto(name, description)) },
            converter = { it.toDomain() },
        ).recoverCatching { throw it.toCourseDetailsException() }
    }

    override suspend fun changeUserRole(
        courseId: String,
        userId: String,
        newRole: CourseDetailsRole,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        safeApiCallUnit {
            api.changeUserRole(courseId = courseId, userId = userId, newRole = newRole.toApiValue())
        }.recoverCatching { throw it.toCourseDetailsException() }
    }
}

class CourseDetailsException(
    val code: Int,
    override val message: String,
) : Exception(message)

private fun Throwable.toCourseDetailsException(): CourseDetailsException = when (this) {
    is CourseDetailsException -> this
    is ApiException -> CourseDetailsException(code = code, message = message)
    else -> CourseDetailsException(code = -1, message = message ?: "Failed to load course details")
}
