package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.Pages


data class Related (

  @SerializedName("pages" ) var pages : ArrayList<Pages> = arrayListOf()

)