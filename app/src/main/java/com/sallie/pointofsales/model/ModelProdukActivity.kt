package com.sallie.pointofsales.model

data class ModelProdukActivity(
    var idProduk: String? = null,
    var namaProduk: String? = null,
    var kategori: String? = null,
    var cabang: String? = null,
    var hargaBeli: Int? = 0,
    var profit: Int? = 0,
    var hargaJual: Int? = 0,
    var stok: Int? = 0,
    var productImageUrl: String? = ""
)
