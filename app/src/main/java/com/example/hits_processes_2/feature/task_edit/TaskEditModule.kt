package com.example.hits_processes_2.feature.task_edit

import com.example.hits_processes_2.feature.task_edit.data.remote.TaskEditApi
import com.example.hits_processes_2.feature.task_edit.data.repository.TaskEditRepositoryImpl
import com.example.hits_processes_2.feature.task_edit.domain.repository.TaskEditRepository
import com.example.hits_processes_2.feature.task_edit.domain.usecase.EditTaskUseCase
import com.example.hits_processes_2.feature.task_edit.domain.usecase.GetTaskEditDetailsUseCase
import com.example.hits_processes_2.feature.task_edit.presentation.TaskEditViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val taskEditModule = module {

    single<TaskEditApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(TaskEditApi::class.java)
    }

    single<TaskEditRepository> { TaskEditRepositoryImpl(get()) }

    factory { GetTaskEditDetailsUseCase(get()) }
    factory { EditTaskUseCase(get()) }

    viewModel { parameters ->
        TaskEditViewModel(
            courseId = parameters.get(),
            taskId = parameters.get(),
            getTaskEditDetailsUseCase = get(),
            editTaskUseCase = get(),
            uploadFileAttachmentUseCase = get(),
            strings = get(),
        )
    }
}
