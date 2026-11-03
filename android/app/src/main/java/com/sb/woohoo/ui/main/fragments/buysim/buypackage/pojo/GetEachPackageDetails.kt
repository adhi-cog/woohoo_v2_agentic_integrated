package com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GetEachPackageDetails (
  @SerializedName("id"        ) var id        : String?           = null,
  @SerializedName("name"      ) var name      : String?           = null,
  @SerializedName("price"     ) var price     : Int?              = null,
  @SerializedName("usdPrice"  ) var usdPrice  : String?           = null,
  @SerializedName("priceUnit" ) var priceUnit : String?           = null,
  @SerializedName("info"      ) var info      : ArrayList<String> = arrayListOf(),
  @SerializedName("createdAt" ) var createdAt : String?           = null,
  @SerializedName("updatedAt" ) var updatedAt : String?           = null,
  @SerializedName("domainId"  ) var domainId  : Int?              = null
): Serializable