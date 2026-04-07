package com.example.hits_processes_2.feature.course_detail.domain.usecase

import com.example.hits_processes_2.feature.course_detail.domain.repository.CourseDetailsRepository

class EditCourseUseCase(
    private val repository: CourseDetailsRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        name: String,
        description: String,
    ) = repository.editCourse(courseId, name, description)
}
