package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName


data class SaudiTourismDestinationsResponse (
  @SerializedName("status"  ) var status  : Int?    = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : ArrayList<Data?>   = arrayListOf(),
  @SerializedName("error"   ) var error   : String? = null

)