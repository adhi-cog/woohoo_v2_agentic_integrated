package com.sb.woohoo.ui.main.fragments.feedback.pojo

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

    var rating: Int = 0
        private set

    var feedBack: String? = ""
        private set

    constructor() {}
    protected constructor(`in`: Parcel) {
        voiceOutput = `in`.readString()
        question = `in`.readString()
        this.category = `in`.readString()
        mediaUrle = `in`.readString()
        rating = `in`.readInt()
        feedBack = `in`.readString()
    }

    fun setMediaUrl(mediaUrl: String?) {
        mediaUrle = mediaUrl
    }

    fun setRating(rating: Int){
        this.rating = rating
    }

    fun setFeedback(feedback: String){
        this.feedBack = feedback
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(voiceOutput)
        dest.writeString(question)
        dest.writeString(this.category)
        dest.writeString(mediaUrle)
        dest.writeInt(rating)
        dest.writeString(feedBack)
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