package com.sallie.pointofsales.model

data class ModelTransaksi(
    val idTransaksi: String = "",
    val tanggal: String = "",
    val namaCabang: String = "",
    val totalBayar: Int = 0,
    val itemTerjual: List<ItemKeranjang> = emptyList()
)

data class ItemKeranjang(
    val idProduk: String = "",
    val namaProduk: String = "",
    val hargaJual: Int = 0,
    var jumlahBeli: Int = 0,
    var subTotal: Int = 0
)
