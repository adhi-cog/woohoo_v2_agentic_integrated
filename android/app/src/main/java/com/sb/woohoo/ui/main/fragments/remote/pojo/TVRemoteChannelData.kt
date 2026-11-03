package com.sb.woohoo.ui.main.fragments.remote.pojo

import com.google.gson.annotations.SerializedName


data class TVRemoteChannelData(
    @SerializedName("actionId") var actionId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("actionData") var actionData: TVRemoteChannelActionData? = TVRemoteChannelActionData()
)