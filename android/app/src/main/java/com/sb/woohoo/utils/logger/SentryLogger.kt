package com.sb.woohoo.utils.logger

import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.protocol.User

class SentryLogger(val defaultTag: String = "") : Logger {

    override fun d(tag: String, message: String) {
        Sentry.captureMessage(tag + "::" + message, SentryLevel.DEBUG)
    }

    override fun e(tag: String, message: String) {
        Sentry.captureException( Throwable(tag + "::" + message))
    }
}