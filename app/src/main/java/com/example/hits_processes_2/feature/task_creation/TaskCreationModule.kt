package com.example.hits_processes_2.feature.task_creation

import com.example.hits_processes_2.feature.task_creation.presentation.TaskCreationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val taskCreationModule = module {
    viewModel { TaskCreationViewModel() }
}
