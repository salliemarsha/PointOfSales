package com.sallie.pointofsales.printer

import com.sallie.pointofsales.transaksi.ItemKeranjang

data class ReceiptTemplate(
    val idTransaksi: String,
    val tanggal: String,
    val namaCabang: String,
    val items: List<ItemKeranjang>,
    val total: Int,
    val bayar: Int,
    val kembalian: Int
)