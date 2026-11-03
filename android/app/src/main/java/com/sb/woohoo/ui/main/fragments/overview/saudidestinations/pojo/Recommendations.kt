package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.Items


data class Recommendations (

  @SerializedName("title" ) var title : String?          = null,
  @SerializedName("items" ) var items : ArrayList<Items> = arrayListOf()

)