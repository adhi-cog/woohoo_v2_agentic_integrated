package com.sb.woohoo.ui.main.sip.state

sealed class SipRegistrationState {

    object RegistrationFailed  : SipRegistrationState()

    object RegistrationSuccess  : SipRegistrationState()

    object RegistrationInProgress : SipRegistrationState()

    object RegistrationInNone : SipRegistrationState()

    object RegistrationInCleared : SipRegistrationState()

}