package com.sb.woohoo.ui.main.fragments.feedback.pojo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewsData : Parcelable {
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("totalResults")
    @Expose
    var totalResults: Int? = null
    @SerializedName("articles")
    @Expose
    var articles: List<Question>? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        status = `in`.readString()
        totalResults = `in`.readValue(Int::class.java.classLoader) as Int
        articles = `in`.createTypedArrayList<Question>(Question.CREATOR)

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(status)
        dest.writeValue(totalResults)
        dest.writeTypedList(articles)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<NewsData?> = object : Parcelable.Creator<NewsData?> {
            override fun createFromParcel(source: Parcel): NewsData? {
                return NewsData(source)
            }

            override fun newArray(size: Int): Array<NewsData?> {
                return arrayOfNulls(size)
            }
        }
    }
}