package com.example.hits_processes_2.feature.task_detail

import com.example.hits_processes_2.feature.task_detail.data.remote.TaskDetailApi
import com.example.hits_processes_2.feature.task_detail.data.repository.TaskDetailRepositoryImpl
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTaskDetailUseCase
import com.example.hits_processes_2.feature.task_detail.presentation.TaskDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val taskDetailModule = module {

    single<TaskDetailApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(TaskDetailApi::class.java)
    }

    single<TaskDetailRepository> { TaskDetailRepositoryImpl(get()) }

    factory { GetTaskDetailUseCase(get()) }

    viewModel { parameters ->
        TaskDetailViewModel(
            courseId = parameters.get(),
            taskId = parameters.get(),
            userRoleName = parameters.get(),
            getTaskDetailUseCase = get(),
            strings = get(),
        )
    }
}
