package com.sb.woohoo.ui.main.pojo.sip

import com.google.gson.annotations.SerializedName

data class SIPConfigurationResponse (
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = Data(),
    @SerializedName("error"   ) var error   : String? = null
)