package com.uhuy.noctura.data.network

import com.uhuy.noctura.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    suspend fun getNews(
        @Header("Authorization") token: String,
        @Query("q") query: String = "sleep research",
    ): NewsResponse
}