package com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo

import com.google.gson.annotations.SerializedName

data class OtpVerifyResponse (
  @SerializedName("status"  ) var status  : Int?            = null,
  @SerializedName("message" ) var message : String?         = null,
  @SerializedName("error"   ) var error   : String?         = null
)