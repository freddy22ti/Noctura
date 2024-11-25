package com.uhuy.noctura.data.network

import com.uhuy.noctura.data.model.News
import com.uhuy.noctura.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String = "sleep research",
    ): NewsResponse
}