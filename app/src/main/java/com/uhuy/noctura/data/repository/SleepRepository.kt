package com.uhuy.noctura.data.repository

import android.util.Log
import com.uhuy.noctura.data.model.SleepItem
import com.uhuy.noctura.data.model.SleepPostRequest
import com.uhuy.noctura.data.model.SleepResponse
import com.uhuy.noctura.data.network.CrudApiService

class SleepRepository(
    private val api: CrudApiService,
) {
    //    API KEY CRUD API
    private val tokenBearer = "Bearer EH9rMX8tUIZDFA4SZdUg95tuydIaWIOYCe4H03e4cFSOm5VFJw"

    //    Mengambil data dari CRUD API
    suspend fun fetchSleepData(): SleepResponse {
        return api.getSleepData(tokenBearer) // Pastikan token disesuaikan
    }

    //    Menyimpan data di CRUD API
    suspend fun createSleepData(sleepData: List<SleepPostRequest>): SleepResponse {
        return api.createSleepData(tokenBearer, sleepData)
    }

    //    Menghapus data di CRUD API
    suspend fun deleteSleepData(uuid: String): SleepItem {
        return api.deleteSleepData(tokenBearer, uuid)
    }
}