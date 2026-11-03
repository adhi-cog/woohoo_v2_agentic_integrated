package com.sb.woohoo.analytics

interface UserProperty {
    companion object {
        const val LOGIN_ID = "login_id"
        const val DEVICE_ID = "device_id"
        const val DOMAIN_ID = "domain_id"

        const val USER_ID = "userId";
        const val SDK_NAME = "sdkName";
        const val SDK_VERSION = "sdkVersion";
        const val MODEL = "model";
        const val OEM_NAME = "oemName";
        const val OS_NAME = "osName";
        const val OS_VERSION = "osVersion";
        const val OS_API_LEVEL = "osApiLevel";
        const val LOCALE = "locale";
        const val TIME_STAMP = "timestamp"; // Format: '2017-03-13T18:05:42Z'
        const val TIME_ZONE_OFFSET = "timeZoneOffset";
        const val SCREEN_SIZE = "screenSize";
        const val APP_VERSION = "appVersion";
        const val APP_BUILD = "appBuild";
        const val CARRIER_NAME = "carrierName";
        const val CARRIER_COUNTRY = "carrierCountry";
        const val APP_NAMESPACE = "appNamespace"; // package name

        const val DEVICE_ORIENTATION = "orientation"
        const val NETWORK_CONNECTION_STATUS = "internet"
    }
}