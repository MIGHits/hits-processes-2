package com.example.hits_processes_2.feature.profile.data.remote

import com.example.hits_processes_2.feature.profile.data.remote.dto.ProfileUserDto
import com.example.hits_processes_2.feature.profile.domain.model.ProfileUser

fun ProfileUserDto.toDomain(): ProfileUser = ProfileUser(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    city = null,
    birthDate = null,
)
