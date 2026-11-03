package com.sb.woohoo.ui.main.fragments.buysim.cart.pojo

import com.google.gson.annotations.SerializedName


data class BuyPackageRequest (
  @SerializedName("email" ) var email : String? = null,
  @SerializedName("packageId" ) var packageId : String? = null,
  @SerializedName("numberId" ) var numberId : String? = null
)