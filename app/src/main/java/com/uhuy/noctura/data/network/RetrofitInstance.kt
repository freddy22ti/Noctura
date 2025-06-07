package com.uhuy.noctura.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL_NEWS = "https://newsapi.org/v2/"
    private const val BASE_URL_CRUD_API = "https://crudapi.co.uk/api/v1/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    fun getNewsApi(): NewsApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_NEWS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    fun getCrudApi(): CrudApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_CRUD_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CrudApiService::class.java)
    }
}