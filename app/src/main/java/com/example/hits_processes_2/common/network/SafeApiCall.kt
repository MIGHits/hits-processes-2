package com.example.hits_processes_2.common.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response

class ApiException(
    val code: Int,
    override val message: String,
) : Exception(message)

private val errorJson = Json {
    ignoreUnknownKeys = true
}

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<ApiResponseDto<T>>,
    converter: (T) -> R,
): Result<R> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            val data = body?.data
            when {
                data != null -> Result.success(converter(data))
                body?.errorMessage != null -> {
                    Result.failure(ApiException(body.statusCode ?: response.code(), body.errorMessage))
                }
                else -> Result.failure(ApiException(response.code(), "Empty response body"))
            }
        } else {
            Result.failure(ApiException(response.code(), response.extractErrorMessage()))
        }
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}

suspend fun safeApiCallUnit(
    apiCall: suspend () -> Response<ApiResponseDto<JsonElement>>,
): Result<Unit> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(ApiException(response.code(), response.extractErrorMessage()))
        }
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}

private fun Response<*>.extractErrorMessage(): String {
    val rawBody = errorBody()?.string().orEmpty()
    if (rawBody.isBlank()) {
        return message().ifBlank { "Unknown network error" }
    }

    val parsedMessage = runCatching {
        errorJson.parseToJsonElement(rawBody)
            .jsonObject["errorMessage"]
            ?.jsonPrimitive
            ?.contentOrNull
    }.getOrNull()

    return parsedMessage ?: rawBody
}
