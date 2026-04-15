package com.example.hits_processes_2.feature.course_detail

import com.example.hits_processes_2.feature.course_detail.data.remote.CourseDetailsApi
import com.example.hits_processes_2.feature.course_detail.data.repository.CourseDetailsRepositoryImpl
import com.example.hits_processes_2.feature.course_detail.domain.repository.CourseDetailsRepository
import com.example.hits_processes_2.feature.course_detail.domain.usecase.ChangeUserRoleUseCase
import com.example.hits_processes_2.feature.course_detail.domain.usecase.EditCourseUseCase
import com.example.hits_processes_2.feature.course_detail.domain.usecase.GetCourseDetailsUseCase
import com.example.hits_processes_2.feature.course_detail.presentation.CourseDetailsViewModel
import com.example.hits_processes_2.feature.profile.domain.usecase.GetMyProfileUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val courseDetailModule = module {
    single<CourseDetailsApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(CourseDetailsApi::class.java)
    }

    single<CourseDetailsRepository> { CourseDetailsRepositoryImpl(get()) }

    factory { GetCourseDetailsUseCase(get()) }
    factory { EditCourseUseCase(get()) }
    factory { ChangeUserRoleUseCase(get()) }

    viewModel { (courseId: String) ->
        CourseDetailsViewModel(
            courseId = courseId,
            getCourseDetailsUseCase = get(),
            editCourseUseCase = get(),
            changeUserRoleUseCase = get(),
            getMyProfileUseCase = get<GetMyProfileUseCase>(),
        )
    }
}
