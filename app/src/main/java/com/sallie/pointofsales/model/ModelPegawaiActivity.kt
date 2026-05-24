package com.sallie.pointofsales.model

import android.os.Parcel
import android.os.Parcelable

data class ModelPegawaiActivity(
    val idPegawai: String? = null,
    val namaPegawai: String? = null,
    val phonePegawai: String? = null,
    val rolePegawai: String? = null,
    val pinPegawai: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idPegawai)
        parcel.writeString(namaPegawai)
        parcel.writeString(phonePegawai)
        parcel.writeString(rolePegawai)
        parcel.writeString(pinPegawai)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ModelPegawaiActivity> {
        override fun createFromParcel(parcel: Parcel): ModelPegawaiActivity {
            return ModelPegawaiActivity(parcel)
        }

        override fun newArray(size: Int): Array<ModelPegawaiActivity?> {
            return arrayOfNulls(size)
        }
    }
}