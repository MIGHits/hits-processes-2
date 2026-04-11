package com.example.hits_processes_2.feature.file_attachment.data.repository

import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.file_attachment.data.remote.FileAttachmentApi
import com.example.hits_processes_2.feature.file_attachment.data.remote.FileMultipartFactory
import com.example.hits_processes_2.feature.file_attachment.data.remote.toDomain
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileAttachmentRepositoryImpl(
    private val api: FileAttachmentApi,
    private val multipartFactory: FileMultipartFactory,
) : FileAttachmentRepository {

    override suspend fun uploadFile(file: FileAttachmentUpload): Result<UploadedFileAttachment> =
        withContext(Dispatchers.IO) {
            runCatching { multipartFactory.create(file) }
                .fold(
                    onSuccess = { part ->
                        safeApiCall(
                            apiCall = { api.uploadFile(part) },
                            converter = { it.toDomain() },
                        )
                    },
                    onFailure = {
                        Result.failure(IllegalStateException("Не удалось подготовить файл"))
                    },
                )
                .mapErrorMessage("Не удалось загрузить файл")
        }
}

private fun <T> Result<T>.mapErrorMessage(defaultMessage: String): Result<T> {
    return recoverCatching { exception ->
        throw IllegalStateException(exception.message ?: defaultMessage, exception)
    }
}
