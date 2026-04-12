package com.example.hits_processes_2.feature.file_attachment.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadedFileDto(
    @SerialName("id") val id: String,
    @SerialName("fileName") val fileName: String,
)
