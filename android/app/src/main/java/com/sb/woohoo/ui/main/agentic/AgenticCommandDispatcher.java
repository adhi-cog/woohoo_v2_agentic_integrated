package com.sb.woohoo.ui.main.agentic;

import com.sb.woohoo.ui.main.pojo.agentic.AgenticDeviceCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Decides what to do with a command handed over by the agentic platform.
 *
 * Deliberately free of Android types so it can be unit tested on the JVM. The routing rules
 * here are the part that silently breaks a room if they are wrong, and "we ran it on a
 * device once" is not a way to keep them right.
 *
 * Three things have to be decided before a command is executed:
 *
 *  - Has it already run? The platform derives commandId from the turn, so a retried turn
 *    repeats the id rather than minting a new one. That makes duplicates detectable here,
 *    which matters because the hub retries NLP requests up to three times on failure and a
 *    response can arrive after a retry has already been sent.
 *
 *  - Is it still relevant? A volume change that arrives ninety seconds late because a
 *    network stalled is not what the guest asked for. Expired commands are dropped.
 *
 *  - Where does it go? Most intents resolve through IntentProcessor exactly as an NLP
 *    intentAction did. A handful open a fragment directly, because MainRepository always
 *    special-cased them and that second dispatch path still exists.
 */
public final class AgenticCommandDispatcher {

    /** What the caller should do with a command. */
    public enum Decision {
        DISPATCH,
        SKIP_DUPLICATE,
        SKIP_EXPIRED,
        SKIP_MALFORMED
    }

    /** Screens reached directly rather than through the intent registry. */
    public static final String SCREEN_FNB = "fnb";
    public static final String SCREEN_FAQ = "faq";
    public static final String SCREEN_HOUSEKEEPING = "housekeeping";
    public static final String SCREEN_HOTEL_INFO = "hotel_info";
    public static final String SCREEN_BOOK_SERVICE = "book_service";
    public static final String SCREEN_FEEDBACK = "feedback";
    public static final String SCREEN_RESTAURANTS = "restaurants";
    public static final String SCREEN_RELAXATION_MUSIC = "relaxation_music";
    public static final String SCREEN_BUY_SIM = "buy_sim";
    public static final String SCREEN_TRANSLATE = "translate";
    public static final String SCREEN_MUSIC_SOURCE = "music_source";

    private static final Map<String, String> DIRECT_SCREENS;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("FnB.Show", SCREEN_FNB);
        m.put("hub.skills.faq", SCREEN_FAQ);
        m.put("hub.skills.housekeeping", SCREEN_HOUSEKEEPING);
        m.put("hub.skills.hotelinfo", SCREEN_HOTEL_INFO);
        m.put("hub.skills.service.book", SCREEN_BOOK_SERVICE);
        m.put("hub.skills.feedback", SCREEN_FEEDBACK);
        m.put("hub.skills.restaurants.show", SCREEN_RESTAURANTS);
        m.put("hub.skills.restaurants.book_table", SCREEN_RESTAURANTS);
        m.put("hub.skills.translate", SCREEN_TRANSLATE);
        m.put("hub.skills.music.choose_source", SCREEN_MUSIC_SOURCE);
        m.put("hub.skills.buy_sim.show", SCREEN_BUY_SIM);
        m.put("hub.skills.buy_sim.select_package", SCREEN_BUY_SIM);
        m.put("hub.skills.relaxation_music.show", SCREEN_RELAXATION_MUSIC);
        m.put("hub.skills.relaxation_music.play", SCREEN_RELAXATION_MUSIC);
        m.put("hub.skills.relaxation_music.pause", SCREEN_RELAXATION_MUSIC);
        m.put("hub.skills.relaxation_music.resume", SCREEN_RELAXATION_MUSIC);
        m.put("hub.skills.relaxation_music.next", SCREEN_RELAXATION_MUSIC);
        m.put("hub.skills.relaxation_music.previous", SCREEN_RELAXATION_MUSIC);
        DIRECT_SCREENS = Collections.unmodifiableMap(m);
    }

    /**
     * Room devices are the property's, not this device's.
     *
     * The hub still carries the lighting stack from its consumer days, which discovers and
     * drives bulbs on the local network. In a hotel the lights belong to the property
     * controller and are driven server-side through a vendor adapter. A lighting intent
     * executed here would therefore succeed against the wrong devices while the room never
     * moved. The platform refuses to issue these; this is the second line of defence, for a
     * stale or tampered response.
     */
    private static final String FORBIDDEN_PREFIX = "hub.devices.";

    /** Bounded memory of executed ids. Small on purpose: duplicates arrive seconds apart. */
    private static final int REMEMBERED = 64;

    private final Set<String> executed = new LinkedHashSet<>();

    public static final class Outcome {
        public final Decision decision;
        public final String reason;

        Outcome(Decision decision, String reason) {
            this.decision = decision;
            this.reason = reason;
        }

        public boolean shouldDispatch() {
            return decision == Decision.DISPATCH;
        }
    }

    /**
     * Whether this command should run now. Does not mutate state, so a caller can evaluate
     * without committing; call {@link #markExecuted} once it has actually been dispatched.
     */
    public Outcome evaluate(AgenticDeviceCommand command, long nowMillis) {
        if (command == null
                || command.getCommandId() == null
                || command.getCommandId().isEmpty()
                || command.getIntent() == null
                || command.getIntent().isEmpty()) {
            return new Outcome(Decision.SKIP_MALFORMED, "command is missing an id or intent");
        }
        if (command.getIntent().startsWith(FORBIDDEN_PREFIX)) {
            return new Outcome(
                    Decision.SKIP_MALFORMED,
                    "room devices are driven by the property controller, not by this device");
        }
        if (executed.contains(command.getCommandId())) {
            return new Outcome(Decision.SKIP_DUPLICATE, "already executed in this session");
        }
        if (isExpired(command.getExpiresAt(), nowMillis)) {
            return new Outcome(Decision.SKIP_EXPIRED, "expired at " + command.getExpiresAt());
        }
        return new Outcome(Decision.DISPATCH, null);
    }

    public void markExecuted(String commandId) {
        if (commandId == null || commandId.isEmpty()) {
            return;
        }
        executed.add(commandId);
        while (executed.size() > REMEMBERED) {
            String oldest = executed.iterator().next();
            executed.remove(oldest);
        }
    }

    /**
     * Screen key for an intent the app opens directly, or null when it should go through
     * IntentProcessor like any other intent.
     */
    public static String directScreenFor(String intent) {
        return intent == null ? null : DIRECT_SCREENS.get(intent);
    }

    public static boolean isForbidden(String intent) {
        return intent != null && intent.startsWith(FORBIDDEN_PREFIX);
    }

    /**
     * An absent or unparseable expiry is treated as NOT expired.
     *
     * The alternative is dropping a perfectly good command because a clock or a format
     * disagreed, and a guest whose alarm silently never got set has no way to tell. A stale
     * command is the lesser harm, and the platform always sends this field.
     */
    static boolean isExpired(String expiresAtIso, long nowMillis) {
        if (expiresAtIso == null || expiresAtIso.isEmpty()) {
            return false;
        }
        try {
            return IsoTime.parseMillis(expiresAtIso) < nowMillis;
        } catch (Exception parseFailure) {
            return false;
        }
    }
}
