package com.example.hits_processes_2.feature.task_detail

import com.example.hits_processes_2.feature.task_detail.data.remote.TaskDetailApi
import com.example.hits_processes_2.feature.task_detail.data.repository.TaskDetailRepositoryImpl
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository
import com.example.hits_processes_2.feature.task_detail.domain.usecase.AttachTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetAllUserTaskAnswersUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetMyTeamUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTeamFinalAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTaskDetailUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.SubmitTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.UnattachTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.UnsubmitTaskAnswerUseCase
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
    factory { GetMyTeamUseCase(get()) }
    factory { AttachTaskAnswerUseCase(get()) }
    factory { GetAllUserTaskAnswersUseCase(get()) }
    factory { GetTeamFinalAnswerUseCase(get()) }
    factory { SubmitTaskAnswerUseCase(get()) }
    factory { UnattachTaskAnswerUseCase(get()) }
    factory { UnsubmitTaskAnswerUseCase(get()) }

    viewModel { parameters ->
        TaskDetailViewModel(
            courseId = parameters.get(),
            taskId = parameters.get(),
            userRoleName = parameters.get(),
            getTaskDetailUseCase = get(),
            getDraftUseCase = get(),
            getMyTeamUseCase = get(),
            attachTaskAnswerUseCase = get(),
            getAllUserTaskAnswersUseCase = get(),
            getTeamFinalAnswerUseCase = get(),
            submitTaskAnswerUseCase = get(),
            unsubmitTaskAnswerUseCase = get(),
            unattachTaskAnswerUseCase = get(),
            deleteFileAttachmentUseCase = get(),
            getTeamsUseCase = get(),
            strings = get(),
        )
    }
}
