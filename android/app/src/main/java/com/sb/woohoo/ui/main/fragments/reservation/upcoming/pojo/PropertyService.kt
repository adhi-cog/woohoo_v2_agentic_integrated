package com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo

import java.io.Serializable


data class PropertyService (
    var id : Int = 0,
    var timeSlot : Int = 0,
    var perSlotLimit  : Int = 0,
    var perBookingLimit  : Int = 0,
    var bookingLimit  : Int = 0,
    var perUserBookingLimit  : Int = 0,
    var domainId  : Int = 0,
    var propertyId  : Int = 0,
    var name: String? = null,
    var location: String? = null,
    var description: String? = null,
    var thumbnailUrl: String? = null,
    var backgroundImageUrl: String? = null,
    var tabletBackgroundImageUrl: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var deletedAt: String? = null,
    var start_time: String? = null,
    var end_time: String? = null

) : Serializable