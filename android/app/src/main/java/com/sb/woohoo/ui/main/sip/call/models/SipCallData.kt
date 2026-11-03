package com.sb.woohoo.ui.main.sip.call.models

data class SipCallData @JvmOverloads constructor(
    val address: String,
    val displayName: String? = null,
    val displayNumber: String? = null,
    val imageUrl: String? = null
)
