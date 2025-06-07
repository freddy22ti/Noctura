package com.uhuy.noctura.data.model

// Objek yang diterima dari CRUD API
data class SleepResponse(
    val cursor: String,
    val items: List<SleepItem>,
    val next_page: String,
)