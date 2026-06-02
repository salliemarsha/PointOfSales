package com.sallie.pointofsales.model

import java.io.Serializable

data class ModelTransaksi(
    val idTransaksi: String = "",
    val tanggal: String = "",
    val namaCabang: String = "",
    val totalBayar: Int = 0,
    val uangBayar: Int = 0,
    val kembalian: Int = 0,
    val itemTerjual: List<ItemKeranjang> = emptyList()
) : Serializable

data class ItemKeranjang(
    val idProduk: String = "",
    val namaProduk: String = "",
    val hargaJual: Int = 0,
    var jumlahBeli: Int = 0,
    var subTotal: Int = 0
) : Serializable
