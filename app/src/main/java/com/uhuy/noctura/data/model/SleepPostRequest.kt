package com.uhuy.noctura.data.model

// Objek untuk mengirim data
data class SleepPostRequest(
    val userId: String,
    val date: String,
    val sleepTime: String,
    val wakeTime: String,
    val sleepDuration: Float,
    val awakenings: Int,
    val caffeineConsumption: Int,
    val alcoholConsumption: Int,
    var sleepQuality: Float? = null,
)