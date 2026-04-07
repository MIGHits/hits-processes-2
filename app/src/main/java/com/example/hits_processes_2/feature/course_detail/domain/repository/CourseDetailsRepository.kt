package com.example.hits_processes_2.feature.course_detail.domain.repository

import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetails
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseTask

interface CourseDetailsRepository {
    suspend fun getCourse(courseId: String): Result<CourseDetails>
    suspend fun getTasks(courseId: String): Result<List<CourseTask>>
    suspend fun getParticipants(courseId: String): Result<List<CourseParticipant>>
    suspend fun editCourse(courseId: String, name: String, description: String): Result<CourseDetails>
    suspend fun changeUserRole(
        courseId: String,
        userId: String,
        newRole: CourseDetailsRole,
    ): Result<Unit>
}
