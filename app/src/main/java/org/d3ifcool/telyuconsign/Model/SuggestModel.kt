package org.d3ifcool.telyuconsign.Model

import android.os.Parcel
import android.os.Parcelable

data class SuggestModel(
    val uid : String,
    val id : String,
    val itemName : String,
    val image : String,
    val price : String,
    val condition : String,
    val desc : String,
    val categories : String,
    val longitude : String,
    val latitude : String,

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image)
        parcel.writeString(itemName)
        parcel.writeString(desc)
        parcel.writeString(price)
        parcel.writeString(condition)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SuggestModel> {
        override fun createFromParcel(parcel: Parcel): SuggestModel {
            return SuggestModel(parcel)
        }

        override fun newArray(size: Int): Array<SuggestModel?> {
            return arrayOfNulls(size)
        }
    }

}
