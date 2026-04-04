package com.example.hits_processes_2.feature.authorization.presentation.validators

import android.util.Patterns

private const val PASSWORD_MIN_LENGTH = 8
private const val PASSWORD_MAX_LENGTH = 32
private val nameRegex = Regex("^[А-Яа-яЁё]{2,32}$")

fun isEmailValid(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isPasswordValid(password: String): Boolean =
    password.length in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH

fun isNameValid(value: String): Boolean = nameRegex.matches(value.trim())
