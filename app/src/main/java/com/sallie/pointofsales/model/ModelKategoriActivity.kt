package com.sallie.pointofsales.model

import android.os.Parcel
import android.os.Parcelable

data class ModelKategoriActivity(
    var idKategori: String? = null,
    var namaKategori: String? = null,
    var statusKategori: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idKategori)
        parcel.writeString(namaKategori)
        parcel.writeString(statusKategori)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ModelKategoriActivity> {
        override fun createFromParcel(parcel: Parcel): ModelKategoriActivity {
            return ModelKategoriActivity(parcel)
        }

        override fun newArray(size: Int): Array<ModelKategoriActivity?> {
            return arrayOfNulls(size)
        }
    }
}