package com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GetPhoneDetails (
  @SerializedName("id"        ) var id        : String?  = null,
  @SerializedName("name"      ) var name      : String?  = null,
  @SerializedName("status"    ) var status    : Boolean? = null,
  @SerializedName("createdAt" ) var createdAt : String?  = null,
  @SerializedName("updatedAt" ) var updatedAt : String?  = null,
  @SerializedName("domainId"  ) var domainId  : Int?     = null

): Serializable