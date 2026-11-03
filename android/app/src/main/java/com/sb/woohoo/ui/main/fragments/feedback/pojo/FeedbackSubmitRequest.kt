package com.sb.woohoo.ui.main.fragments.feedback.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FeedbackSubmitRequest {

    @SerializedName("deviceId")
    @Expose
    var deviceId: String? = null
    @SerializedName("overallRating")
    @Expose
    var overallRating: String? = null
    @SerializedName("feedback")
    @Expose
    var feedback: List<FeedbackSubmitList>? = null

    class FeedbackSubmitList {
        @SerializedName("category")
        @Expose
        var category: String? = null
        @SerializedName("rating")
        @Expose
        var rating: String? = null

        @SerializedName("feedback")
        @Expose
        var feedback: String? = null
    }
}