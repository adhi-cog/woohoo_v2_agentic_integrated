package com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SaudiTourismPackagesResponse (
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = Data(),
    @SerializedName("error"   ) var error   : String? = null

): Serializable