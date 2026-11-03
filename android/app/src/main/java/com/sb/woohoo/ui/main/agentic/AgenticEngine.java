package com.sb.woohoo.ui.main.agentic;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;

/**
 * Whether this device routes speech to the agentic platform, and where.
 *
 * The app already chose between NLP engines through a stored preference, so the agentic
 * platform is a third value rather than a new mechanism. That matters more than it sounds:
 * it makes the migration reversible per device, from the Settings screen, with no deploy.
 * A room behaving oddly after a rollout can be put back on the engine it had a minute ago,
 * by whoever is standing in front of it.
 *
 * Two conditions have to hold, and both are deliberate:
 *
 *  - The preference says agentic. Operator intent.
 *  - A base URL is compiled in. A build with no gateway configured cannot route to one, so
 *    a mis-set preference degrades to the previous engine instead of failing every turn.
 */
public final class AgenticEngine {

    /** Third value alongside DIALOGFLOW_ENGINE (100) and SMARTFLOW_ENGINE (101). */
    public static final int AGENTIC_ENGINE = 102;

    private AgenticEngine() {
    }

    /**
     * Where the gateway lives.
     *
     * A stored override wins over the compiled-in default, so the same APK can be pointed at
     * a tester's laptop, a hotel LAN or a deployed environment. Without it every environment
     * needs its own build, which is how testers end up running something nobody can reproduce.
     *
     * Set from Settings -> Engine. Deliberately not an exported broadcast receiver: that
     * would let any app on the device repoint the assistant at a server of its choosing.
     */
    public static String baseUrl() {
        String configured = override();
        if (configured == null || configured.trim().isEmpty()) {
            configured = BuildConfig.AGENTIC_BASE_URL;
        }
        if (configured == null) {
            return "";
        }
        String trimmed = configured.trim();
        // Normalise once so callers never produce a double slash, which some gateways treat
        // as a different route entirely.
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    /** Stored override, or null when none is set. */
    public static String override() {
        if (WooHooApp.getSharedPrefsHelper() == null) {
            return null;
        }
        return WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_AGENTIC_BASE_URL, "");
    }

    /** Persist a gateway URL for this device. Empty clears it back to the built-in default. */
    public static void setOverride(String url) {
        if (WooHooApp.getSharedPrefsHelper() == null) {
            return;
        }
        WooHooApp.getSharedPrefsHelper()
                .put(SharedPrefsKey.PREF_KEY_AGENTIC_BASE_URL, url == null ? "" : url.trim());
    }

    public static boolean isConfigured() {
        return !baseUrl().isEmpty();
    }

    /** The stored engine preference, defaulting to the agentic platform when one is built in. */
    public static int selectedEngine() {
        int fallback = isConfigured() ? AGENTIC_ENGINE : 101;
        if (WooHooApp.getSharedPrefsHelper() == null) {
            return fallback;
        }
        return WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_ENGINE, fallback);
    }

    /** True when this turn should go to the agentic gateway. */
    public static boolean isActive() {
        return isConfigured() && selectedEngine() == AGENTIC_ENGINE;
    }

    public static String turnUrl() {
        return baseUrl() + "/v1/turn";
    }

    public static String ackUrl(String commandId) {
        return baseUrl() + "/v1/device-commands/" + commandId + "/ack";
    }
}
