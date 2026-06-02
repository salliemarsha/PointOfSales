package com.sallie.pointofsales.model

data class ModelPelanggan(
    var idPelanggan: String? = null,
    var namaPelanggan: String? = null,
    var nomorTelepon: String? = null,
    var email: String? = null,
    var alamat: String? = null,
    var tanggalDaftar: Long? = null
)
