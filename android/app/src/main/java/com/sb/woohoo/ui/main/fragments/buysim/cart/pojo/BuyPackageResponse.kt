package com.sb.woohoo.ui.main.fragments.buysim.cart.pojo

import com.google.gson.annotations.SerializedName

data class BuyPackageResponse (
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : RedirectUrl?   = RedirectUrl(),
    @SerializedName("error"   ) var error   : String? = null

)