package com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo

import com.google.gson.annotations.SerializedName

data class GetAllPhoneNumbersResponse (
  @SerializedName("status"  ) var status  : Int?            = null,
  @SerializedName("message" ) var message : String?         = null,
  @SerializedName("data"    ) var data    : ArrayList<GetPhoneDetails> = arrayListOf(),
  @SerializedName("error"   ) var error   : String?         = null
)