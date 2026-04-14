package com.example.hits_processes_2.feature.voting.presentation

data class VotingOption(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val solutionTitle: String,
)

val VotingOption.fullName: String
    get() = "$firstName $lastName"
