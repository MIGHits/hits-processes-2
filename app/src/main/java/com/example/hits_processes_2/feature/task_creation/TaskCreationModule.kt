package com.example.hits_processes_2.feature.task_creation

import com.example.hits_processes_2.feature.file_attachment.domain.usecase.UploadFileAttachmentUseCase
import com.example.hits_processes_2.feature.task_creation.data.remote.TaskCreationApi
import com.example.hits_processes_2.feature.task_creation.data.repository.TaskCreationRepositoryImpl
import com.example.hits_processes_2.feature.task_creation.domain.repository.TaskCreationRepository
import com.example.hits_processes_2.feature.task_creation.domain.usecase.CreateTaskUseCase
import com.example.hits_processes_2.feature.task_creation.presentation.TaskCreationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val taskCreationModule = module {

    single<TaskCreationApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(TaskCreationApi::class.java)
    }

    single<TaskCreationRepository> { TaskCreationRepositoryImpl(get(), get()) }

    factory { CreateTaskUseCase(get()) }

    viewModel { parameters ->
        TaskCreationViewModel(
            createTaskUseCase = get(),
            uploadFileAttachmentUseCase = get<UploadFileAttachmentUseCase>(),
            strings = get(),
            courseId = parameters.getOrNull(),
        )
    }
}
