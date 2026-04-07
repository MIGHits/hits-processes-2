package com.example.hits_processes_2.feature.course_detail.domain.usecase

import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetails
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseTask
import com.example.hits_processes_2.feature.course_detail.domain.repository.CourseDetailsRepository

class GetCourseDetailsUseCase(
    private val repository: CourseDetailsRepository,
) {
    suspend operator fun invoke(courseId: String): Triple<Result<CourseDetails>, Result<List<CourseTask>>, Result<List<CourseParticipant>>> {
        return Triple(
            repository.getCourse(courseId),
            repository.getTasks(courseId),
            repository.getParticipants(courseId),
        )
    }
}
