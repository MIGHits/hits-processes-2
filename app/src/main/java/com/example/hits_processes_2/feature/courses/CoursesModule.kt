package com.example.hits_processes_2.feature.courses

import com.example.hits_processes_2.feature.courses.data.remote.CoursesApi
import com.example.hits_processes_2.feature.courses.data.repository.CoursesRepositoryImpl
import com.example.hits_processes_2.feature.courses.domain.repository.CoursesRepository
import com.example.hits_processes_2.feature.courses.domain.usecase.CreateCourseUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.GetMyCoursesUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.GetMyProfileUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.JoinCourseUseCase
import com.example.hits_processes_2.feature.courses.presentation.CoursesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val coursesModule = module {

    single<CoursesApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(CoursesApi::class.java)
    }

    single<CoursesRepository> { CoursesRepositoryImpl(get()) }

    factory { GetMyCoursesUseCase(get()) }
    factory { CreateCourseUseCase(get()) }
    factory { JoinCourseUseCase(get()) }
    factory { GetMyProfileUseCase(get()) }

    viewModel { CoursesViewModel(get(), get(), get(), get(), get()) }
}
