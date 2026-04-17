package com.sallie.pointofsales.model

import android.os.Parcel
import android.os.Parcelable

data class ModelProdukActivity(
    val idProduk: String? = null,
    val namaProduk: String? = null,
    val kategori: String? = null,
    val cabang: String? = null,
    val hargaBeli: Int? = 0,
    val profit: Int? = 0,
    val hargaJual: Int? = 0,
    val stok: Int? = 0,
    val tanpaBatas: Boolean? = false,
    val fotoProduk: String? = null,
    val statusProduk: String? = "aktif",
    var createdAt: String? = null,
    var updatedAt: String? = null

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idProduk)
        parcel.writeString(namaProduk)
        parcel.writeString(kategori)
        parcel.writeString(cabang)
        parcel.writeValue(hargaBeli)
        parcel.writeValue(profit)
        parcel.writeValue(hargaJual)
        parcel.writeValue(stok)
        parcel.writeByte(if (tanpaBatas == true) 1 else 0)
        parcel.writeString(fotoProduk)
        parcel.writeString(statusProduk)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ModelProdukActivity> {
        override fun createFromParcel(parcel: Parcel): ModelProdukActivity {
            return ModelProdukActivity(parcel)
        }

        override fun newArray(size: Int): Array<ModelProdukActivity?> {
            return arrayOfNulls(size)
        }
    }
}