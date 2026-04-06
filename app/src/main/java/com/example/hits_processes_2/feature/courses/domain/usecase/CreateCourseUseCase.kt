package com.example.hits_processes_2.feature.courses.domain.usecase

import com.example.hits_processes_2.feature.courses.domain.repository.CoursesRepository

class CreateCourseUseCase(
    private val coursesRepository: CoursesRepository,
) {
    suspend operator fun invoke(
        name: String,
        description: String,
    ) = coursesRepository.createCourse(
        name = name,
        description = description,
    )
}
