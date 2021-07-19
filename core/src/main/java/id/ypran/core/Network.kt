package id.ypran.core

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun createNetworkClient(baseUrl: String) = retrofitClient(baseUrl)

fun retrofitClient(baseUrl: String): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(
        OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = BASIC
            }).build()
    )
    .addConverterFactory(GsonConverterFactory.create())
    .build()
