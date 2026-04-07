package com.example.hits_processes_2.feature.course_detail.domain.usecase

import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.course_detail.domain.repository.CourseDetailsRepository

class ChangeUserRoleUseCase(
    private val repository: CourseDetailsRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        userId: String,
        newRole: CourseDetailsRole,
    ) = repository.changeUserRole(courseId, userId, newRole)
}
