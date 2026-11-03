package com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo

import com.google.gson.annotations.SerializedName


data class EmailIdOtpRequest (
  @SerializedName("email" ) var email : String? = null,
  @SerializedName("otp" ) var otp : String? = null

)