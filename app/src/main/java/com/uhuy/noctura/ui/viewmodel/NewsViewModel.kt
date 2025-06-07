package com.uhuy.noctura.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhuy.noctura.data.model.NewsResponse
import com.uhuy.noctura.data.repository.NewsRepository
import com.uhuy.noctura.utils.NetworkUtils
import com.uhuy.noctura.utils.Resource
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _data = MutableLiveData<Resource<NewsResponse>>()
    val data: LiveData<Resource<NewsResponse>> = _data

    fun getNewsData(context: Context, forceRefresh: Boolean = false) {
        if(_data.value == null || forceRefresh) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                fetchNewsData()
            } else {
                _data.postValue(Resource.Error("No internet connection"))
            }

        }
    }

    private fun fetchNewsData() {
        viewModelScope.launch {
            _data.value = Resource.Loading()
            try {
                val response = newsRepository.fetchNews()
                Log.e("NewsViewModel", response.status)
                if(response.totalResults > 0) {
                    Log.e("NewsViewModel", "success")
                    _data.postValue(Resource.Success(response))
                } else {
                    _data.postValue(Resource.Empty("No data found"))
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error fetching news: ${e.message}", e) // Log the exception
                _data.value = Resource.Error("Unknown error: ${e.message}")
            }
        }
    }
}