package com.example.hits_processes_2.feature.draft.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DraftApi {
    @GET("api/course/task/draft/{draftId}")
    suspend fun getDraft(
        @Path("draftId") draftId: String,
    ): Response<ApiResponseDto<DraftDto>>
}
