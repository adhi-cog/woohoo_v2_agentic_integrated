package com.sb.woohoo.ui.main.fragments.remote.pojo

import com.google.gson.annotations.SerializedName

data class TVRemoteChannelResponse (
  @SerializedName("status"  ) var status  : Int?            = null,
  @SerializedName("message" ) var message : String?         = null,
  @SerializedName("data"    ) var data    : ArrayList<TVRemoteChannelData> = arrayListOf(),
  @SerializedName("error"   ) var error   : String?         = null
)