package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName


data class Details (

  @SerializedName("title" ) var title : String?          = null,
  @SerializedName("items" ) var items : ArrayList<Items> = arrayListOf()

)