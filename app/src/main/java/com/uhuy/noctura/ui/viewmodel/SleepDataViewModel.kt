package com.uhuy.noctura.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhuy.noctura.data.model.SleepPostRequest
import com.uhuy.noctura.data.model.SleepResponse
import com.uhuy.noctura.data.repository.SleepRepository
import com.uhuy.noctura.utils.NetworkUtils
import com.uhuy.noctura.utils.Resource
import kotlinx.coroutines.launch

class SleepDataViewModel(private val repository: SleepRepository) : ViewModel() {
    //data di-set sebagai LiveData agar UI dapat melakukan observe status saat mengambildata
    private val _data = MutableLiveData<Resource<SleepResponse>>()
    val data: LiveData<Resource<SleepResponse>> = _data

    // createStatus di-set sebagai LiveData agar UI dapat melakukan observe status saat mengirim data
    private val _createStatus = MutableLiveData<Resource<Unit>>()
    val createStatus: LiveData<Resource<Unit>> = _createStatus

    // LiveData for observing delete status
    private val _deleteStatus = MutableLiveData<Resource<Unit>>()
    val deleteStatus: LiveData<Resource<Unit>> = _deleteStatus

    fun getSleepData(context: Context, forceRefresh: Boolean = false) {
        if (_data.value == null || forceRefresh) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                fetchSleepData()
            } else {
                _data.postValue(Resource.Error("No internet connection"))
            }
        }
    }

    private fun fetchSleepData() {
        viewModelScope.launch {
            _data.value = Resource.Loading()
            try {
                val response = repository.fetchSleepData()
                if (response.items.isEmpty()) {
                    _data.postValue(Resource.Empty("No data found"))
                } else {
                    _data.postValue(Resource.Success(response))
                }
            } catch (e: Exception) {
                _data.value = Resource.Error("Unknown error: ${e.message}")
            }
        }
    }

    fun createSleepData(context: Context, sleepData: List<SleepPostRequest>) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _createStatus.value = Resource.Loading()

                    val response = repository.createSleepData(sleepData)
                    if (response.items.isEmpty()) {
                        _data.postValue(Resource.Empty("No data found"))
                    } else {
                        _data.postValue(Resource.Success(response))
                    }
                    // Refresh data setelah create sukses
                    getSleepData(context, forceRefresh = true)
                } catch (e: Exception) {
                    _data.postValue(Resource.Error("Unknown error: ${e.message}"))
                }
            }
        } else {
            _createStatus.postValue(Resource.Error("No internet connection"))
        }
    }

    fun deleteSleepData(context: Context, uuid: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _deleteStatus.value = Resource.Loading()
                    repository.deleteSleepData(uuid)
                    _deleteStatus.postValue(Resource.Success(Unit))
                    // Refresh the data after deletion
                    getSleepData(context, forceRefresh = true)
                } catch (e: Exception) {
                    _deleteStatus.postValue(Resource.Error("Unknown error: ${e.message}"))
                }
            }
        } else {
            _deleteStatus.postValue(Resource.Error("No internet connection"))
        }
    }
}