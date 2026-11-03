package com.sb.woohoo.ui.main.sip.state

sealed class SipCallActionState {
    object Accept :  SipCallActionState()
    object End :  SipCallActionState()
    object Reject :  SipCallActionState()
    object Mute :  SipCallActionState()
    object Unmute :  SipCallActionState()
    object SpeakerOn :  SipCallActionState()
    object SpeakerOff :  SipCallActionState()
    class outgoingCallInit @JvmOverloads constructor(
        val sipAddress: String,
        val displayName: String? = null,
        val displayNumber: String? = null,
        val imageUrl: String? = null
    ) :  SipCallActionState()
}
