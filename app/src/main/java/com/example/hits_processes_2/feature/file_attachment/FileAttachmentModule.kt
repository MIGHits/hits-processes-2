package com.example.hits_processes_2.feature.file_attachment

import com.example.hits_processes_2.feature.file_attachment.data.remote.FileAttachmentApi
import com.example.hits_processes_2.feature.file_attachment.data.remote.FileMultipartFactory
import com.example.hits_processes_2.feature.file_attachment.data.repository.FileAttachmentRepositoryImpl
import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository
import com.example.hits_processes_2.feature.file_attachment.domain.usecase.UploadFileAttachmentUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val fileAttachmentModule = module {

    single<FileAttachmentApi> {
        get<Retrofit>(named("authenticatedRetrofit")).create(FileAttachmentApi::class.java)
    }

    single { FileMultipartFactory(get()) }

    single<FileAttachmentRepository> { FileAttachmentRepositoryImpl(get(), get()) }

    factory { UploadFileAttachmentUseCase(get()) }
}
