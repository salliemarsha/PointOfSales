package com.sallie.pointofsales.transaksi

data class ItemKeranjang(
    val namaProduk: String,
    val hargaProduk: Int,
    var jumlahBeli: Int
) {
    val subTotal: Int
        get() = hargaProduk * jumlahBeli
}