package com.example.hits_processes_2.feature.voting.presentation

data class VotingSolutionFile(
    val id: String,
    val name: String,
)

data class VotingOption(
    val id: String,
    val firstName: String,
    val lastName: String,
    val solutionFiles: List<VotingSolutionFile> = emptyList(),
    val votesCount: Int = 0,
)

val VotingOption.fullName: String
    get() = "$firstName $lastName"
