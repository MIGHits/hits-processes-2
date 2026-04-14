package com.example.hits_processes_2.common.network

import com.example.hits_processes_2.feature.authorization.data.SessionExpiredNotifierImpl
import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.authorization.data.TokenStorageImpl
import com.example.hits_processes_2.feature.authorization.data.remote.AuthApi
import com.example.hits_processes_2.feature.authorization.data.remote.AuthInterceptor
import com.example.hits_processes_2.feature.authorization.data.remote.SessionExpiredInterceptor
import com.example.hits_processes_2.feature.authorization.data.remote.TokenAuthenticator
import com.example.hits_processes_2.feature.authorization.domain.SessionExpiredNotifier
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://91.227.18.176/"
private const val NETWORK_TIMEOUT_SECONDS = 30L

val networkModule = module {

    single<TokenStorage> { TokenStorageImpl(get()) }

    single<SessionExpiredNotifier> { SessionExpiredNotifierImpl() }

    single {
        val tokenStorage: TokenStorage = get()
        AuthInterceptor(tokenProvider = { tokenStorage.getTokens()?.accessToken })
    }

    single {
        SessionExpiredInterceptor(
            tokenStorage = get(),
            sessionExpiredNotifier = get(),
        )
    }

    single(named("json")) {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single(named("authClient")) {
        createHttpClient(
            authInterceptor = get(),
            sessionExpiredInterceptor = get(),
            tokenAuthenticator = null,
        )
    }

    single(named("authRetrofit")) {
        createRetrofit(
            json = get(named("json")),
            client = get(named("authClient")),
        )
    }

    single<AuthApi> {
        get<Retrofit>(named("authRetrofit")).create(AuthApi::class.java)
    }

    single {
        TokenAuthenticator(
            tokenStorage = get(),
            authApi = get(),
            sessionExpiredNotifier = get(),
        )
    }

    single(named("authenticatedClient")) {
        createHttpClient(
            authInterceptor = get(),
            sessionExpiredInterceptor = get(),
            tokenAuthenticator = get(),
        )
    }

    single(named("authenticatedRetrofit")) {
        createRetrofit(
            json = get(named("json")),
            client = get(named("authenticatedClient")),
        )
    }
}

private fun createHttpClient(
    authInterceptor: AuthInterceptor,
    sessionExpiredInterceptor: SessionExpiredInterceptor,
    tokenAuthenticator: TokenAuthenticator?,
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(sessionExpiredInterceptor)
        .apply {
            if (tokenAuthenticator != null) {
                authenticator(tokenAuthenticator)
            }
        }
        .addInterceptor(createLoggingInterceptor())
        .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
}

private fun createRetrofit(
    json: Json,
    client: OkHttpClient,
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

private fun createLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
