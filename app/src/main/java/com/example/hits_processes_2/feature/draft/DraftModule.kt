package com.example.hits_processes_2.feature.draft

import com.example.hits_processes_2.feature.draft.data.remote.DraftApi
import com.example.hits_processes_2.feature.draft.data.repository.DraftRepositoryImpl
import com.example.hits_processes_2.feature.draft.domain.repository.DraftRepository
import com.example.hits_processes_2.feature.draft.domain.usecase.GetDraftUseCase
import com.example.hits_processes_2.feature.draft.domain.usecase.ObserveDraftUseCase
import com.example.hits_processes_2.feature.draft.presentation.DraftViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val draftModule = module {
    single<DraftApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(DraftApi::class.java)
    }

    single(named("draftWebSocketClient")) {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<DraftRepository> {
        DraftRepositoryImpl(
            api = get(),
            tokenStorage = get(),
            okHttpClient = get<OkHttpClient>(named("draftWebSocketClient")),
            json = get(named("json")),
        )
    }

    factory { GetDraftUseCase(get()) }

    factory { ObserveDraftUseCase(get()) }

    viewModel { DraftViewModel(get(), get(), get(), get(), get()) }
}
