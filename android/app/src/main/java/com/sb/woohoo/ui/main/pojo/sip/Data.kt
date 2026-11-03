package com.sb.woohoo.ui.main.pojo.sip

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Data(

    @SerializedName("username") var username: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("roomId") var roomId: Int? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("domain") var domain: String? = null,
    @SerializedName("transport") var transport: String? = null,
    @SerializedName("proxy") var proxy: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("deletedAt") var deletedAt: String? = null,

    ) : Serializable