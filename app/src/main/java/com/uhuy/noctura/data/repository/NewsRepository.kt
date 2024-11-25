package com.uhuy.noctura.data.repository

import android.util.Log
import com.uhuy.noctura.data.model.News
import com.uhuy.noctura.data.network.ApiService

class NewsRepository(private val api: ApiService) {
    suspend fun fetchNews(): List<News>? {
        val response = api.getNews()
        if (response.status == "ok") {
            return response.articles
        }
        return null
    }
}