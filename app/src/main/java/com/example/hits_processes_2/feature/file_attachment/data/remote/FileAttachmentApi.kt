package com.example.hits_processes_2.feature.file_attachment.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.file_attachment.data.remote.dto.UploadedFileDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface FileAttachmentApi {

    @Multipart
    @POST("api/file/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
    ): Response<ApiResponseDto<UploadedFileDto>>

    @Streaming
    @GET("api/file/{fileId}")
    suspend fun downloadFile(
        @Path("fileId") fileId: String,
    ): Response<ResponseBody>
}
