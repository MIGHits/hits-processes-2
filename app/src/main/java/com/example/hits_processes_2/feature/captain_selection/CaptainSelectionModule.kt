package com.example.hits_processes_2.feature.captain_selection

import com.example.hits_processes_2.feature.captain_selection.data.repository.CaptainSelectionRepositoryImpl
import com.example.hits_processes_2.feature.captain_selection.domain.repository.CaptainSelectionRepository
import com.example.hits_processes_2.feature.captain_selection.domain.usecase.AssignDraftCaptainUseCase
import com.example.hits_processes_2.feature.captain_selection.domain.usecase.GetCaptainSelectionUseCase
import com.example.hits_processes_2.feature.captain_selection.domain.usecase.RemoveDraftCaptainUseCase
import com.example.hits_processes_2.feature.captain_selection.presentation.CaptainSelectionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val captainSelectionModule = module {
    single<CaptainSelectionRepository> { CaptainSelectionRepositoryImpl(get()) }

    factory { GetCaptainSelectionUseCase(get()) }

    factory { AssignDraftCaptainUseCase(get()) }

    factory { RemoveDraftCaptainUseCase(get()) }

    viewModel { CaptainSelectionViewModel(get(), get(), get()) }
}
