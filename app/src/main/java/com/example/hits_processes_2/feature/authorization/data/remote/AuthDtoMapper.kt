package com.example.hits_processes_2.feature.authorization.data.remote

import com.example.hits_processes_2.feature.authorization.data.remote.dto.TokenResponseDto
import com.example.hits_processes_2.feature.authorization.data.remote.dto.UserLoginDto
import com.example.hits_processes_2.feature.authorization.data.remote.dto.UserRegisterDto
import com.example.hits_processes_2.feature.authorization.domain.model.RegisterData
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import com.example.hits_processes_2.feature.authorization.domain.model.UserCredentials

fun TokenResponseDto.toDomain(): TokenPair = TokenPair(
    accessToken = accessToken,
    refreshToken = refreshToken,
)

fun UserCredentials.toLoginDto(): UserLoginDto = UserLoginDto(
    email = email,
    password = password,
)

fun RegisterData.toRegisterDto(): UserRegisterDto = UserRegisterDto(
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
)
