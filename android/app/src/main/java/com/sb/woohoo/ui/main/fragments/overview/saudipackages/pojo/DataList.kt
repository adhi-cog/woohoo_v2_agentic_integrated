package com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class DataList (

  @SerializedName("id"                      ) var id                    : String?           = null,
  @SerializedName("name"                    ) var name                  : String?           = null,
  @SerializedName("description"             ) var description           : String?           = null,
  @SerializedName("slug"                    ) var slug                  : String?           = null,
  @SerializedName("price_start_from"        ) var priceStartFrom        : String?           = null,
  @SerializedName("city"                    ) var city                  : String?           = null,
  @SerializedName("city_id"                 ) var cityId                : String?           = null,
  @SerializedName("primary_destination"     ) var primaryDestination    : String?           = null,
  @SerializedName("secondary_destinations"  ) var secondaryDestinations : ArrayList<String> = arrayListOf(),
  @SerializedName("list_price"              ) var listPrice             : Int              = 0,
  @SerializedName("discounted_price"        ) var discountedPrice       : Int?              = null,
  @SerializedName("background_image"        ) var backgroundImage       : String?           = null,
  @SerializedName("background_images"       ) var backgroundImages      : ArrayList<String> = arrayListOf(),
  @SerializedName("book_online"             ) var bookOnline            : Boolean?          = null,
  @SerializedName("min_capacity"            ) var minCapacity           : Int?              = null,
  @SerializedName("duration"                ) var duration              : Int?              = null,
  @SerializedName("duration_unit"           ) var durationUnit          : String?           = null,
  @SerializedName("category_name"           ) var categoryName          : String?           = null,
  @SerializedName("category_slug"           ) var categorySlug          : String?           = null,
  @SerializedName("contact_name"            ) var contactName           : String?           = null,
  @SerializedName("price_type"              ) var priceType             : String?           = null,
  @SerializedName("price_type_capacity"     ) var priceTypeCapacity     : Int?              = null,
  @SerializedName("package_url"             ) var packageUrl            : String?           = null,
  @SerializedName("is_dmc"                  ) var isDmc                 : Boolean?          = null,
  @SerializedName("has_waivers_booking"     ) var hasWaiversBooking     : Boolean?          = null,
  @SerializedName("waivers_view"            ) var waiversView           : ArrayList<String> = arrayListOf(),
  @SerializedName("waivers_required_input"  ) var waiversRequiredInput  : ArrayList<String> = arrayListOf(),
  @SerializedName("latitude"                ) var latitude              : Double?           = null,
  @SerializedName("longitude"               ) var longitude             : Double?           = null,
  @SerializedName("with_flexible_pass"      ) var withFlexiblePass      : Boolean?          = null,
  @SerializedName("flexible_pass_valid_for" ) var flexiblePassValidFor  : Int?              = null


): Serializable






data class PackageDetailsData (

        @SerializedName("id"                      ) var id                    : String?           = null,
        @SerializedName("name"                    ) var name                  : String?           = null,
        @SerializedName("description"             ) var description           : String?           = null,
        @SerializedName("slug"                    ) var slug                  : String?           = null,
        @SerializedName("price_start_from"        ) var priceStartFrom        : String?           = null,
        @SerializedName("city"                    ) var city                  : String?           = null,
        @SerializedName("city_id"                 ) var cityId                : String?           = null,
        @SerializedName("primary_destination"     ) var primaryDestination    : String?           = null,
        @SerializedName("secondary_destinations"  ) var secondaryDestinations : ArrayList<String> = arrayListOf(),
        @SerializedName("list_price"              ) var listPrice             : Int?              = null,
        @SerializedName("discounted_price"        ) var discountedPrice       : Int?              = null,
        @SerializedName("background_image"        ) var backgroundImage       : String?           = null,
        @SerializedName("background_images"       ) var backgroundImages      : ArrayList<String> = arrayListOf(),
        @SerializedName("book_online"             ) var bookOnline            : Boolean?          = null,
        @SerializedName("min_capacity"            ) var minCapacity           : Int?              = null,
        @SerializedName("duration"                ) var duration              : Int?              = null,
        @SerializedName("duration_unit"           ) var durationUnit          : String?           = null,
        @SerializedName("category_name"           ) var categoryName          : String?           = null,
        @SerializedName("category_slug"           ) var categorySlug          : String?           = null,
        @SerializedName("contact_name"            ) var contactName           : String?           = null,
        @SerializedName("price_type"              ) var priceType             : String?           = null,
        @SerializedName("price_type_capacity"     ) var priceTypeCapacity     : Int?              = null,
        @SerializedName("package_url"             ) var packageUrl            : String?           = null,
        @SerializedName("is_dmc"                  ) var isDmc                 : Boolean?          = null,
        @SerializedName("has_waivers_booking"     ) var hasWaiversBooking     : Boolean?          = null,
        @SerializedName("waivers_view"            ) var waiversView           : ArrayList<String> = arrayListOf(),
        @SerializedName("waivers_required_input"  ) var waiversRequiredInput  : ArrayList<String> = arrayListOf(),
        @SerializedName("latitude"                ) var latitude              : Double?           = null,
        @SerializedName("longitude"               ) var longitude             : Double?           = null,
        @SerializedName("with_flexible_pass"      ) var withFlexiblePass      : Boolean?          = null,
        @SerializedName("flexible_pass_valid_for" ) var flexiblePassValidFor  : Int?              = null


): Serializable