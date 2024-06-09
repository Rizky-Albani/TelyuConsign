package org.d3ifcool.telyuconsign.Model

import android.os.Parcel
import android.os.Parcelable

data class BarangRoomModel(
    var imageBarang : Int,
    var namaBarang : String,
    var deskripsiBarang : String,
    var hargaBarang : String,
    var kondisiBarang : String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageBarang)
        parcel.writeString(namaBarang)
        parcel.writeString(deskripsiBarang)
        parcel.writeString(hargaBarang)
        parcel.writeString(kondisiBarang)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BarangRoomModel> {
        override fun createFromParcel(parcel: Parcel): BarangRoomModel {
            return BarangRoomModel(parcel)
        }

        override fun newArray(size: Int): Array<BarangRoomModel?> {
            return arrayOfNulls(size)
        }
    }

}
