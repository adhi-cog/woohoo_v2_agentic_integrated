package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName


data class Links (

  @SerializedName("id"    ) var id    : String? = null,
  @SerializedName("link"  ) var link  : String? = null,
  @SerializedName("title" ) var title : String? = null,
  @SerializedName("type"  ) var type  : String? = null

)