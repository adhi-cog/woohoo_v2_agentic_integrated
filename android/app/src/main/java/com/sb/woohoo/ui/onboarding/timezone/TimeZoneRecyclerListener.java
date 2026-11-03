package com.sb.woohoo.ui.onboarding.timezone;

import java.util.TimeZone;

/**
 * Listener for RemoteAlertModel Clicked on RecyclerView for {@link TimeZoneAdapter}.
 *
 * @author Ritwik Jamuar.
 */
public interface TimeZoneRecyclerListener {
    void onTimeZoneClicked(TimeZone timeZone);
}