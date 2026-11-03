package com.sb.woohoo.ui.main.fragments.covid.pojo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class QuestionList protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("questions")
    @Expose
    var question: List<Question>? = null

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeTypedList(question)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<QuestionList?> = object : Parcelable.Creator<QuestionList?> {
            override fun createFromParcel(`in`: Parcel): QuestionList? {
                return QuestionList(`in`)
            }

            override fun newArray(size: Int): Array<QuestionList?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        question = `in`.createTypedArrayList<Question>(Question.CREATOR)
    }
}