package com.sb.woohoo.ui.main.fragments.buysim.emailverification.pojo

import com.google.gson.annotations.SerializedName

data class OtpSendResponse (
  @SerializedName("status"  ) var status  : Int?            = null,
  @SerializedName("message" ) var message : String?         = null,
  @SerializedName("error"   ) var error   : String?         = null
)