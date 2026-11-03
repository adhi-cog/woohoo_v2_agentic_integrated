package com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo

import com.google.gson.annotations.SerializedName

data class GetAllPackagesResponse(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<GetEachPackageDetails> = arrayListOf(),
    @SerializedName("error") var error: String? = null
)