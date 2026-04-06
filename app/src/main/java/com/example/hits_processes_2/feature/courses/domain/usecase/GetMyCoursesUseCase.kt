package com.example.hits_processes_2.feature.courses.domain.usecase

import com.example.hits_processes_2.feature.courses.domain.repository.CoursesRepository

class GetMyCoursesUseCase(
    private val coursesRepository: CoursesRepository,
) {
    suspend operator fun invoke() = coursesRepository.getMyCourses()
}
