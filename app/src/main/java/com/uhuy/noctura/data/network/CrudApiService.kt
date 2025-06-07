package com.uhuy.noctura.data.network

import com.uhuy.noctura.data.model.SleepItem
import com.uhuy.noctura.data.model.SleepPostRequest
import com.uhuy.noctura.data.model.SleepResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CrudApiService {
    @POST("sleepData")
    suspend fun createSleepData(
        @Header("Authorization") token: String,
        @Body sleepData: List<SleepPostRequest>,
    ): SleepResponse

    @GET("sleepData")
    suspend fun getSleepData(
        @Header("Authorization") token: String
    ): SleepResponse

    @DELETE("sleepData/{uuid}")
    suspend fun deleteSleepData(
        @Header("Authorization") token: String,
        @Path("uuid") uuid: String
    ): SleepItem
}