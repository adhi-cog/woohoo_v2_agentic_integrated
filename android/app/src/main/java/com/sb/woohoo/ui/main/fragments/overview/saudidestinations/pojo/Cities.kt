package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class DataItem (

  @SerializedName("id"                 ) var id                 : String?           = null,
  @SerializedName("title"              ) var title              : String?           = null,
  @SerializedName("heading"            ) var heading            : String?           = null,
  @SerializedName("description"        ) var description        : String?           = null,
  @SerializedName("image"              ) var image              : String?           = null,
  @SerializedName("imageCaption"       ) var imageCaption       : ImageCaption?     = ImageCaption(),
  @SerializedName("latitude"           ) var latitude           : String?           = null,
  @SerializedName("longitude"          ) var longitude          : String?           = null,
  @SerializedName("details"            ) var details            : Details?          = Details(),
  @SerializedName("recommendations"    ) var recommendations    : Recommendations?  = Recommendations(),
  @SerializedName("related"            ) var related            : Related?          = Related(),
  @SerializedName("regionId"           ) var regionId           : String?           = null,
  @SerializedName("areasAround"        ) var areasAround        : ArrayList<String> = arrayListOf(),
  @SerializedName("photoGallery"       ) var photoGallery       : ArrayList<String> = arrayListOf(),
  @SerializedName("packages"           ) var packages           : ArrayList<String> = arrayListOf(),
  @SerializedName("previewImage"       ) var previewImage       : String?           = null,
  @SerializedName("mapBackgroundImage" ) var mapBackgroundImage : String?           = null,
  @SerializedName("links" ) var links : LinkData?           = null,
  @SerializedName("bestFor"            ) var bestFor            : ArrayList<String> = arrayListOf(),
  @SerializedName("entertainerOffer"   ) var entertainerOffer   : EntertainerOffer? = EntertainerOffer(),
  @SerializedName("promote"            ) var promote            : Boolean?          = null

): Serializable