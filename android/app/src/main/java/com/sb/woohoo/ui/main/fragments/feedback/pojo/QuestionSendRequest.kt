package com.sb.woohoo.ui.main.fragments.feedback.pojo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class QuestionSendRequest : Parcelable {
    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("error")
    @Expose
    var error: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        status = `in`.readValue(Int::class.java.classLoader) as Int
        message = `in`.readString()
        error = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(status)
        dest.writeString(message)
        dest.writeString(error)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<QuestionSendRequest?> = object : Parcelable.Creator<QuestionSendRequest?> {
            override fun createFromParcel(source: Parcel): QuestionSendRequest? {
                return QuestionSendRequest(source)
            }

            override fun newArray(size: Int): Array<QuestionSendRequest?> {
                return arrayOfNulls(size)
            }
        }
    }
}