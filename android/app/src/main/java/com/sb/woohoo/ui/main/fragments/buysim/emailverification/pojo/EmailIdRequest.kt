package com.sb.woohoo.ui.main.fragments.buysim.emailverification.pojo

import com.google.gson.annotations.SerializedName


data class EmailIdRequest (
  @SerializedName("email" ) var email : String? = null

)