package com.uhuy.noctura.data.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val umur: Int = 0,
    val jenisKelamin: String = "",
    val smokingStatus: Boolean = false,
    val frekuensiOlahraga: Int = 0,
)