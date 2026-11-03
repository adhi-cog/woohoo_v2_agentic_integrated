package com.sb.woohoo.analytics

interface CustomEvents {
    companion object {
        const val QUICK_LINKS_FNB = "quicklink_fnb"
        const val QUICK_LINKS_FEEDBACK = "quicklink_feedback"
        const val QUICK_LINKS_QRCODE = "quicklink_qrcode"
        const val QUICK_LINKS_NIGHT_MODE = "quicklink_nightmode"
        const val QUICK_LINKS_RECEPTION = "quicklink_reception"
        const val SOCKET_CALL = "socket_call"

        const val category_name_tourism = "Tourism"
        const val event_name_st = "SaudiTourism.PlaceOrder"
        const val destinations_name_st = "SaudiTourism.Destinations"
        const val events_details_name_st = "SaudiTourism.Events"

        const val category_name_fnb = "F&B"
        const val event_name_add_to_cart = "FnB.AddToCart"
    }
}