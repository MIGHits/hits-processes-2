package com.example.hits_processes_2.feature.courses.domain.repository

import com.example.hits_processes_2.feature.courses.domain.model.CourseShort
import com.example.hits_processes_2.feature.courses.domain.model.UserProfile

interface CoursesRepository {
    suspend fun getMyCourses(): Result<List<CourseShort>>
    suspend fun createCourse(name: String, description: String): Result<CourseShort>
    suspend fun joinCourse(code: String): Result<CourseShort>
    suspend fun getMyProfile(): Result<UserProfile>
}
