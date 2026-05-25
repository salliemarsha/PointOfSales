package com.sallie.pointofsales.model

class ModelProdukActivity {
    var idProduk: String? = null
    var namaProduk: String? = null
    var kategori: String? = null
    var cabang: String? = null
    var hargaBeli: Int? = 0
    var profit: Int? = 0
    var hargaJual: Int? = 0
    var stok: Int? = 0
    var fotoUrl: String? = ""

    constructor()

    constructor(
        idProduk: String?,
        namaProduk: String?,
        kategori: String?,
        cabang: String?,
        hargaBeli: Int?,
        profit: Int?,
        hargaJual: Int?,
        stok: Int?,
        fotoUrl: String? = ""
    ) {
        this.idProduk = idProduk
        this.namaProduk = namaProduk
        this.kategori = kategori
        this.cabang = cabang
        this.hargaBeli = hargaBeli
        this.profit = profit
        this.hargaJual = hargaJual
        this.stok = stok
        this.fotoUrl = fotoUrl
    }
}