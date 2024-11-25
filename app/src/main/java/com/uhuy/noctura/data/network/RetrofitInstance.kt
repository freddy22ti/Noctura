package com.uhuy.noctura.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL_NEWS = "https://newsapi.org/v2/"
    private const val API_KEY = "4786d36736ce42a2b448b44c0e58b76c"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    fun getNewsApi(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_NEWS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}