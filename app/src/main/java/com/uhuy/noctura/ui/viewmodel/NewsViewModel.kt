package com.uhuy.noctura.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.uhuy.noctura.data.model.News
import com.uhuy.noctura.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _newsData = MutableLiveData<List<News>?>()
    val newsData: MutableLiveData<List<News>?> get() = _newsData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchNews() {
        viewModelScope.launch {
            _loading.value = true
            val articles = newsRepository.fetchNews()
            if (articles != null) {
                _newsData.value = articles
            }
            _loading.value = false
        }
    }
}
