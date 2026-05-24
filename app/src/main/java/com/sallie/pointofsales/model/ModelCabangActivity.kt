package com.sallie.pointofsales.model

import android.os.Parcel
import android.os.Parcelable

data class ModelCabangActivity(
    val idCabang: String? = null,
    val namaCabang: String? = null,
    val lokasiCabang: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idCabang)
        parcel.writeString(namaCabang)
        parcel.writeString(lokasiCabang)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ModelCabangActivity> {
        override fun createFromParcel(parcel: Parcel): ModelCabangActivity {
            return ModelCabangActivity(parcel)
        }

        override fun newArray(size: Int): Array<ModelCabangActivity?> {
            return arrayOfNulls(size)
        }
    }
}