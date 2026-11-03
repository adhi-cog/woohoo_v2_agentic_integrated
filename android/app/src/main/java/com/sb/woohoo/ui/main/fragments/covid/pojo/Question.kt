package com.sb.woohoo.ui.main.fragments.covid.pojo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Question : Parcelable {
    @SerializedName("voiceOutput")
    @Expose
    var voiceOutput: String? = null
    @SerializedName("question")
    @Expose
    var question: String? = null
    @SerializedName("category")
    @Expose
    var category: String? = null
    @SerializedName("mediaUrl")
    @Expose
    var mediaUrle: String? = null
        private set

    constructor() {}
    protected constructor(`in`: Parcel) {
        voiceOutput = `in`.readString()
        question = `in`.readString()
        this.category = `in`.readString()
        mediaUrle = `in`.readString()
    }

    fun setMediaUrl(mediaUrl: String?) {
        mediaUrle = mediaUrl
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(voiceOutput)
        dest.writeString(question)
        dest.writeString(this.category)
        dest.writeString(mediaUrle)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Question?> = object : Parcelable.Creator<Question?> {
            override fun createFromParcel(source: Parcel): Question? {
                return Question(source)
            }

            override fun newArray(size: Int): Array<Question?> {
                return arrayOfNulls(size)
            }
        }
    }
}