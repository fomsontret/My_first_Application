package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.AuthResponse

private val authRetrofit = retrofit2.Retrofit.Builder()
    .baseUrl("${BuildConfig.BASE_URL}/api/")
    .addConverterFactory(
        retrofit2.converter.gson.GsonConverterFactory.create()
    )
    .build()

interface AuthApiService {

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authenticate(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthResponse>
}

object AuthApi {
    val service: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }
}