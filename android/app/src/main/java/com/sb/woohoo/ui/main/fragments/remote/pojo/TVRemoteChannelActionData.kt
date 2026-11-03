package com.sb.woohoo.ui.main.fragments.remote.pojo

import com.google.gson.annotations.SerializedName


data class TVRemoteChannelActionData (
  @SerializedName("channelId" ) var channelId : String? = null
)