package com.example.hits_processes_2.feature.courses.data.remote

import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseRoleDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.UserProfileDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseShortDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseShortListDto
import com.example.hits_processes_2.feature.courses.domain.model.CourseRole
import com.example.hits_processes_2.feature.courses.domain.model.CourseShort
import com.example.hits_processes_2.feature.courses.domain.model.UserProfile

fun CourseShortListDto.toDomain(): List<CourseShort> = courseShortList.map(CourseShortDto::toDomain)

fun CourseShortDto.toDomain(): CourseShort = CourseShort(
    id = id,
    name = name,
    description = description,
    currentUserRole = currentUserCourseRole?.toDomain(),
)

fun CourseRoleDto.toDomain(): CourseRole = when (this) {
    CourseRoleDto.STUDENT -> CourseRole.STUDENT
    CourseRoleDto.TEACHER -> CourseRole.TEACHER
    CourseRoleDto.HEAD_TEACHER -> CourseRole.HEAD_TEACHER
}

fun UserProfileDto.toDomain(): UserProfile = UserProfile(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
)
