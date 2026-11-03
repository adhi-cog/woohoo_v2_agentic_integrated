package com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Data (

  @SerializedName("status"        ) var status      : String?         = null,
  @SerializedName("data"          ) var data        : ArrayList<DataList> = arrayListOf(),
  @SerializedName("per_page"      ) var perPage     : Int?            = null,
  @SerializedName("total"         ) var total       : Int?            = null,
  @SerializedName("curr_page"     ) var currPage    : Int?            = null,
  @SerializedName("last_page"     ) var lastPage    : Int?            = null,
  @SerializedName("next_page_url" ) var nextPageUrl : String?         = null

): Serializable