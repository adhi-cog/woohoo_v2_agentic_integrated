package com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo

import java.util.*
import java.io.Serializable

data class UpcomingServicesResponse (
    val status : Int = 0,
    val message: String? = null,
    val data: ArrayList<UpcomingService>? = null,
    val error: String? = null) : Serializable


data class UpcomingService  (
        var id: Int = 0,
        var local_start_time: String? = null,
        var propertyService: PropertyService? = null,
        var startTimeInFormat: String? = null) : Serializable
/*{
    "id": 54,
    "refId": "ymgjrf2v6eryjevjc4wd",
    "start_time": "2021-04-04 04:00 AM",
    "end_time": "2021-04-04 04:30 AM",
    "date": "2021-04-04",
    "local_date": "2021-04-04",
    "local_start_time": "2021-04-04 09:30 AM",
    "local_end_time": "2021-04-04 10:00 AM",
    "timezone": "Asia/Calcutta",
    "count": 1,
    "createdAt": "2021-04-03T03:40:27.980Z",
    "updatedAt": "2021-04-03T03:40:27.980Z",
    "deletedAt": null,
    "domainId": 14,
    "propertyId": 101,
    "serviceId": 4,
    "roomId": null,
    "hubId": "4c86b2c78dafd539",
    "userId": 462,
    "propertyService": {
        "id": 4,
        "name": "Swimming Pool",
        "location": "Ground Floor",
        "description": "capacity for 20 ppl",
        "thumbnailUrl": "https://s3-us-west-2.amazonaws.com/woohoo-enterprise/propertyServices/upload_ea242fa038a92d78513c6bbffe162455.jpg",
        "backgroundImageUrl": "https://s3-us-west-2.amazonaws.com/woohoo-enterprise/propertyServices/upload_dacf38471dc679fd2a04b95274055e77.jpg",
        "tabletBackgroundImageUrl": "",
        "timeSlot": 30,
        "perSlotLimit": 1,
        "perBookingLimit": 2,
        "bookingLimit": 30,
        "perUserBookingLimit": 1,
        "start_time": "06:00 AM",
        "end_time": "06:00 PM",
        "exceptions": [],
        "emails": [],
        "createdAt": "2021-03-18T18:46:02.319Z",
        "updatedAt": "2021-03-18T18:46:02.319Z",
        "deletedAt": null,
        "domainId": 14,
        "propertyId": 101
    }
}*/