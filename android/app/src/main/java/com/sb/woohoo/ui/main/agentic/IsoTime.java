package com.sb.woohoo.ui.main.agentic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Minimal ISO-8601 handling for the agentic command contract.
 *
 * java.time would be the obvious choice and is not available: the app's minSdk is 21 and core
 * library desugaring is not enabled. Turning desugaring on for one timestamp would change the
 * build for every module in a shipping app, so this parses the one shape the platform sends.
 *
 * SimpleDateFormat is not thread safe, hence a new instance per call rather than a shared
 * static. These run once per command, so the allocation is irrelevant next to the bug a
 * shared formatter would eventually cause.
 */
final class IsoTime {

    private IsoTime() {
    }

    /**
     * Epoch millis for an ISO-8601 instant in UTC, with or without fractional seconds.
     *
     * @throws ParseException when the value is not a shape we recognise, which the caller
     *                        treats as "no expiry" rather than as "expired".
     */
    static long parseMillis(String iso) throws ParseException {
        String value = iso.trim();
        // Normalise a trailing offset of +00:00 to Z so one pair of patterns covers both.
        if (value.endsWith("+00:00")) {
            value = value.substring(0, value.length() - 6) + "Z";
        }
        String pattern = value.indexOf('.') >= 0
                ? "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                : "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.setLenient(false);
        return format.parse(value).getTime();
    }

    /** Current time as the ISO-8601 instant the platform expects on an acknowledgement. */
    static String nowIso() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }
}
