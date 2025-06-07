package com.uhuy.noctura.data.model

// Objek yang kita simpan di CRUD SIMPAN
data class SleepItem(
    val _created: Double,
    val _data_type: String,
    val _is_deleted: Boolean,
    val _modified: Double,
    val _self_link: String,
    val _user: String,
    val _uuid: String,

    val userId: String,
    val date: String,
    val sleepTime: String,
    val wakeTime: String,
    val sleepDuration: Float,
    val awakenings: Int,
    val caffeineConsumption: Int,
    val alcoholConsumption: Int,
    var sleepQuality: Float,
)