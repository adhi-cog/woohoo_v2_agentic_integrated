package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("strapline"    ) var strapline    : String?            = null,
  @SerializedName("title"        ) var title        : String?            = null,
  @SerializedName("identifier"   ) var identifier   : String?            = null,
  @SerializedName("id"           ) var id           : String?            = null,
  @SerializedName("description"  ) var description  : String?            = null,
  @SerializedName("image"        ) var image        : String?            = null,
  @SerializedName("mapIcon"      ) var mapIcon      : String?            = null,
  @SerializedName("imageCaption" ) var imageCaption : ImageCaption?      = ImageCaption(),
  @SerializedName("previewImage" ) var previewImage : String?            = null,
  @SerializedName("bestFor"      ) var bestFor      : ArrayList<String>  = arrayListOf(),
  @SerializedName("details"      ) var details      : ArrayList<Details> = arrayListOf(),
  @SerializedName("package_url"  ) var packageUrl   : String?            = null,
  @SerializedName("cities"       ) var cities       : ArrayList<DataItem>  = arrayListOf()

)


data class LinkData(

        @SerializedName("id") var id           : String?            = null,
        @SerializedName("link") var link           : String?            = null,
        @SerializedName("title") var title           : String?            = null

        )
