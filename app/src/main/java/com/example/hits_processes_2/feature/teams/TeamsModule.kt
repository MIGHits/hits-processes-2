package com.example.hits_processes_2.feature.teams

import com.example.hits_processes_2.feature.teams.data.remote.TeamsApi
import com.example.hits_processes_2.feature.teams.data.repository.TeamsRepositoryImpl
import com.example.hits_processes_2.feature.teams.domain.repository.TeamsRepository
import com.example.hits_processes_2.feature.teams.domain.usecase.AddTeamMemberUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.AssignTeamCaptainUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.EvaluateTeamAnswerUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetFreeStudentsUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetTeamsUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.JoinTeamUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.LeaveTeamUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.RemoveTeamMemberUseCase
import com.example.hits_processes_2.feature.teams.presentation.TeamsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val teamsModule = module {

    single<TeamsApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(TeamsApi::class.java)
    }

    single<TeamsRepository> { TeamsRepositoryImpl(get()) }

    factory { GetTeamsUseCase(get()) }

    factory { GetFreeStudentsUseCase(get()) }

    factory { JoinTeamUseCase(get()) }

    factory { LeaveTeamUseCase(get()) }

    factory { AddTeamMemberUseCase(get()) }

    factory { RemoveTeamMemberUseCase(get()) }

    factory { AssignTeamCaptainUseCase(get()) }

    factory { EvaluateTeamAnswerUseCase(get()) }

    viewModel { TeamsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
