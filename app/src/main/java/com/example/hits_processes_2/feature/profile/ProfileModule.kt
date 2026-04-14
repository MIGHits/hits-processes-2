package com.example.hits_processes_2.feature.profile

import com.example.hits_processes_2.feature.profile.data.remote.ProfileApi
import com.example.hits_processes_2.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.hits_processes_2.feature.profile.domain.repository.ProfileRepository
import com.example.hits_processes_2.feature.profile.domain.usecase.GetMyProfileUseCase
import com.example.hits_processes_2.feature.profile.presentation.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val profileModule = module {
    single<ProfileApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(ProfileApi::class.java)
    }

    single<ProfileRepository> { ProfileRepositoryImpl(get()) }

    factory { GetMyProfileUseCase(get()) }

    viewModel { ProfileViewModel(get(), get()) }
}
