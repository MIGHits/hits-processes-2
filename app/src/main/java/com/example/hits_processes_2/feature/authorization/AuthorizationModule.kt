package com.example.hits_processes_2.feature.authorization

import com.example.hits_processes_2.feature.authorization.data.repository.AuthRepositoryImpl
import com.example.hits_processes_2.feature.authorization.domain.repository.AuthRepository
import com.example.hits_processes_2.feature.authorization.domain.usecase.LoginUseCase
import com.example.hits_processes_2.feature.authorization.domain.usecase.LogoutUseCase
import com.example.hits_processes_2.feature.authorization.domain.usecase.RegisterUseCase
import com.example.hits_processes_2.feature.authorization.presentation.AuthorizationScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authorizationModule = module {

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    factory { LoginUseCase(get()) }

    factory { RegisterUseCase(get()) }

    factory { LogoutUseCase(get()) }

    viewModel { AuthorizationScreenViewModel(get(), get()) }
}
