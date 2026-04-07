package com.example.hits_processes_2.feature.course_detail.domain.model

data class CourseParticipantsBundle(
    val teachers: List<CourseParticipant>,
    val students: List<CourseParticipant>,
)
