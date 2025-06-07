package com.uhuy.noctura.data.repository

import android.util.Log
import com.uhuy.noctura.data.model.News
import com.uhuy.noctura.data.model.NewsResponse
import com.uhuy.noctura.data.model.SleepResponse
import com.uhuy.noctura.data.network.NewsApiService

class NewsRepository(private val api: NewsApiService) {
    //    API KEY news api
    private val tokenBearer = "4786d36736ce42a2b448b44c0e58b76c"

    //    Mengambil data dari news api
    suspend fun fetchNews(): NewsResponse {
        return api.getNews(tokenBearer)
    }
}