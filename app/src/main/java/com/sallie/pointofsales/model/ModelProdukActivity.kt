package com.sallie.pointofsales.model

import android.os.Parcel
import android.os.Parcelable
import com.sallie.pointofsales.model.ModelProdukActivity

data class ModelProdukActivity (
    val idProduk: String? = null,
    val namaProduk: String? = null,
    val hargaProduk: Int? = 0,
    val idKategori: String? = null,
    val idCabang: String? = null,
    val fotoProduk: String? = null,
    val stokProduk: Int? = 0,
    val tanpaBatas: Boolean? = false,
    val statusProduk: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idProduk)
        parcel.writeString(namaProduk)
        parcel.writeValue(hargaProduk)
        parcel.writeString(idKategori)
        parcel.writeString(idCabang)
        parcel.writeString(fotoProduk)
        parcel.writeValue(stokProduk)
        parcel.writeByte(if (tanpaBatas == true) 1 else 0)
        parcel.writeString(statusProduk)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelProdukActivity> {

        override fun createFromParcel(parcel: Parcel): ModelProdukActivity {
            return ModelProdukActivity(parcel)
        }

        override fun newArray(size: Int): Array<ModelProdukActivity?> {
            return arrayOfNulls(size)
        }
    }
}