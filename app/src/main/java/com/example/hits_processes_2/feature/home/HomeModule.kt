package com.example.hits_processes_2.feature.home

import com.example.hits_processes_2.feature.home.presentation.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(get()) }
}
