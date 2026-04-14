package com.example.hits_processes_2.feature.course_detail.data.remote

import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseDetailsDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseDetailsRoleDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseTaskShortDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseTaskShortListDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseUserDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseUserListDto
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetails
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseTask

fun CourseDetailsDto.toDomain(): CourseDetails = CourseDetails(
    id = id,
    name = name,
    description = description,
    joinCode = joinCode,
    currentUserRole = currentUserCourseRole?.toDomain() ?: CourseDetailsRole.STUDENT,
)

fun CourseTaskShortListDto.toDomain(): List<CourseTask> {
    val orderedTasks = if (tasks.any { it.createdAt != null }) {
        tasks.sortedByDescending { it.createdAt.orEmpty() }
    } else {
        tasks.asReversed()
    }
    return orderedTasks.map(CourseTaskShortDto::toDomain)
}

fun CourseTaskShortDto.toDomain(): CourseTask = CourseTask(
    id = id,
    title = title,
    text = text,
)

fun CourseUserListDto.toDomain(): List<CourseParticipant> = userCourseList.map(CourseUserDto::toDomain)

fun CourseUserDto.toDomain(): CourseParticipant = CourseParticipant(
    id = userModel.id,
    firstName = userModel.firstName,
    lastName = userModel.lastName,
    email = userModel.email,
    role = userRole.toDomain(),
)

fun CourseDetailsRoleDto.toDomain(): CourseDetailsRole = when (this) {
    CourseDetailsRoleDto.STUDENT -> CourseDetailsRole.STUDENT
    CourseDetailsRoleDto.TEACHER -> CourseDetailsRole.TEACHER
    CourseDetailsRoleDto.HEAD_TEACHER -> CourseDetailsRole.HEAD_TEACHER
}

fun CourseDetailsRole.toApiValue(): String = when (this) {
    CourseDetailsRole.STUDENT -> "STUDENT"
    CourseDetailsRole.TEACHER -> "TEACHER"
    CourseDetailsRole.HEAD_TEACHER -> "HEAD_TEACHER"
}
