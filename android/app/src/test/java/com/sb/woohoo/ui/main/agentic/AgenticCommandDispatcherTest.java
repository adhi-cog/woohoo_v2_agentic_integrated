package com.sb.woohoo.ui.main.agentic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticDeviceCommand;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnResponse;

import org.junit.Before;
import org.junit.Test;

/**
 * Dispatcher rules for commands handed over by the agentic platform.
 *
 * Commands are built from JSON rather than from setters so these also cover the wire format:
 * a field renamed on the platform side shows up here as a null, which is the failure that
 * would otherwise only appear as a command that silently never ran on a real device.
 */
public class AgenticCommandDispatcherTest {

    private static final long NOW = 1_760_000_000_000L;

    private Gson gson;
    private AgenticCommandDispatcher dispatcher;

    @Before
    public void setUp() {
        gson = new Gson();
        dispatcher = new AgenticCommandDispatcher();
    }

    private AgenticDeviceCommand command(String id, String intent, String expiresAt) {
        String json = "{\"commandId\":\"" + id + "\","
                + "\"intent\":\"" + intent + "\","
                + "\"slots\":{\"time\":\"07:00\"},"
                + "\"expiresAt\":\"" + expiresAt + "\"}";
        return gson.fromJson(json, AgenticDeviceCommand.class);
    }

    private static String isoAt(long millis) {
        try {
            java.text.SimpleDateFormat f =
                    new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            f.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return f.format(new java.util.Date(millis));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ── the wire format ──────────────────────────────────────────────────

    @Test
    public void parsesTheCommandThePlatformActuallySends() {
        AgenticDeviceCommand c = command("dc_turn_1", "hub.skills.alarms.add", isoAt(NOW + 60_000));
        assertEquals("dc_turn_1", c.getCommandId());
        assertEquals("hub.skills.alarms.add", c.getIntent());
        assertEquals("07:00", c.getSlots().get("time"));
    }

    @Test
    public void separatesWorkAlreadyDoneFromWorkToDo() {
        // The distinction this whole integration rests on. calls[] has already run server
        // side; dispatching it would fire the vendor command a second time.
        String json = "{\"turnId\":\"turn_8f21c\",\"speech\":\"Turning the volume up.\","
                + "\"calls\":[{\"tool\":\"lighting.set\",\"ok\":true}],"
                + "\"deviceCommands\":[{\"commandId\":\"dc_turn_8f21c_1\","
                + "\"intent\":\"hub.controls.system.volume.up\",\"slots\":{},"
                + "\"expiresAt\":\"" + isoAt(NOW + 60_000) + "\"}]}";
        AgenticTurnResponse r = gson.fromJson(json, AgenticTurnResponse.class);

        assertEquals(1, r.getCalls().size());
        assertEquals(1, r.getDeviceCommands().size());
        assertEquals("lighting.set", r.getCalls().get(0).getTool());
        assertEquals("hub.controls.system.volume.up", r.getDeviceCommands().get(0).getIntent());
    }

    @Test
    public void absentArraysReadAsEmptyRatherThanNull() {
        AgenticTurnResponse r = gson.fromJson("{\"speech\":\"Done.\"}", AgenticTurnResponse.class);
        assertNotNull(r.getCalls());
        assertNotNull(r.getDeviceCommands());
        assertTrue(r.getCalls().isEmpty());
        assertTrue(r.getDeviceCommands().isEmpty());
    }

    @Test
    public void reportsAFailedCallEvenWhenSpeechSoundsFine() {
        String json = "{\"speech\":\"I have asked for that.\","
                + "\"calls\":[{\"tool\":\"lighting.set\",\"ok\":false,\"error\":\"NOT_AVAILABLE\"}]}";
        AgenticTurnResponse r = gson.fromJson(json, AgenticTurnResponse.class);
        assertTrue(r.hasFailedCall());
    }

    // ── duplicates ───────────────────────────────────────────────────────

    @Test
    public void dispatchesACommandTheFirstTime() {
        AgenticDeviceCommand c = command("dc_turn_1", "hub.skills.timer.set", isoAt(NOW + 60_000));
        assertTrue(dispatcher.evaluate(c, NOW).shouldDispatch());
    }

    @Test
    public void refusesTheSameCommandTwice() {
        AgenticDeviceCommand c = command("dc_turn_1", "hub.skills.timer.set", isoAt(NOW + 60_000));
        dispatcher.markExecuted(c.getCommandId());
        AgenticCommandDispatcher.Outcome second = dispatcher.evaluate(c, NOW);
        assertFalse(second.shouldDispatch());
        assertEquals(AgenticCommandDispatcher.Decision.SKIP_DUPLICATE, second.decision);
    }

    @Test
    public void treatsDifferentIdsAsDifferentWork() {
        dispatcher.markExecuted("dc_turn_1_1");
        AgenticDeviceCommand other = command("dc_turn_1_2", "hub.skills.timer.stop", isoAt(NOW + 60_000));
        assertTrue(dispatcher.evaluate(other, NOW).shouldDispatch());
    }

    @Test
    public void forgetsOldIdsWithoutForgettingRecentOnes() {
        for (int i = 0; i < 200; i++) {
            dispatcher.markExecuted("dc_old_" + i);
        }
        AgenticDeviceCommand recent = command("dc_old_199", "hub.skills.timer.set", isoAt(NOW + 60_000));
        // The most recent must still be remembered; a duplicate arrives seconds later.
        assertEquals(
                AgenticCommandDispatcher.Decision.SKIP_DUPLICATE,
                dispatcher.evaluate(recent, NOW).decision);
    }

    // ── expiry ───────────────────────────────────────────────────────────

    @Test
    public void dropsACommandThatArrivedTooLate() {
        AgenticDeviceCommand stale = command("dc_turn_9", "hub.controls.system.volume.up", isoAt(NOW - 1));
        AgenticCommandDispatcher.Outcome o = dispatcher.evaluate(stale, NOW);
        assertFalse(o.shouldDispatch());
        assertEquals(AgenticCommandDispatcher.Decision.SKIP_EXPIRED, o.decision);
    }

    @Test
    public void runsACommandThatIsStillValid() {
        AgenticDeviceCommand fresh = command("dc_turn_9", "hub.controls.system.volume.up", isoAt(NOW + 1_000));
        assertTrue(dispatcher.evaluate(fresh, NOW).shouldDispatch());
    }

    @Test
    public void treatsAnUnparseableExpiryAsNoExpiry() {
        // Dropping a good command because a format disagreed is the worse failure: the guest
        // gets silence and no way to tell why.
        assertFalse(AgenticCommandDispatcher.isExpired("not-a-timestamp", NOW));
        assertFalse(AgenticCommandDispatcher.isExpired(null, NOW));
        assertFalse(AgenticCommandDispatcher.isExpired("", NOW));
    }

    @Test
    public void parsesBothTimestampShapesThePlatformMightSend() throws Exception {
        long withMillis = IsoTime.parseMillis("2026-10-12T13:12:30.500Z");
        long withoutMillis = IsoTime.parseMillis("2026-10-12T13:12:30Z");
        assertEquals(500L, withMillis - withoutMillis);
    }

    @Test
    public void acceptsAZeroOffsetAsUtc() throws Exception {
        assertEquals(
                IsoTime.parseMillis("2026-10-12T13:12:30Z"),
                IsoTime.parseMillis("2026-10-12T13:12:30+00:00"));
    }

    // ── room devices ─────────────────────────────────────────────────────

    @Test
    public void refusesRoomDeviceIntentsEvenIfThePlatformSendsOne() {
        // Second line of defence. The hub's own lighting stack drives bulbs on the local
        // network; the hotel's lights are on the property controller. Running this here
        // would succeed against the wrong devices while the room never moved.
        AgenticDeviceCommand light =
                command("dc_turn_1", "hub.devices.lights.all.toggle.off", isoAt(NOW + 60_000));
        AgenticCommandDispatcher.Outcome o = dispatcher.evaluate(light, NOW);
        assertFalse(o.shouldDispatch());
        assertEquals(AgenticCommandDispatcher.Decision.SKIP_MALFORMED, o.decision);
        assertTrue(o.reason.contains("property controller"));
    }

    @Test
    public void identifiesForbiddenIntents() {
        assertTrue(AgenticCommandDispatcher.isForbidden("hub.devices.lights.scene"));
        assertFalse(AgenticCommandDispatcher.isForbidden("hub.skills.music.play"));
    }

    // ── malformed ────────────────────────────────────────────────────────

    @Test
    public void refusesACommandWithNoIntent() {
        AgenticDeviceCommand c = gson.fromJson("{\"commandId\":\"dc_1\"}", AgenticDeviceCommand.class);
        assertEquals(
                AgenticCommandDispatcher.Decision.SKIP_MALFORMED,
                dispatcher.evaluate(c, NOW).decision);
    }

    @Test
    public void refusesACommandWithNoId() {
        AgenticDeviceCommand c =
                gson.fromJson("{\"intent\":\"hub.skills.timer.set\"}", AgenticDeviceCommand.class);
        assertEquals(
                AgenticCommandDispatcher.Decision.SKIP_MALFORMED,
                dispatcher.evaluate(c, NOW).decision);
    }

    @Test
    public void refusesNull() {
        assertEquals(
                AgenticCommandDispatcher.Decision.SKIP_MALFORMED,
                dispatcher.evaluate(null, NOW).decision);
    }

    // ── routing ──────────────────────────────────────────────────────────

    @Test
    public void routesFnbToItsOwnScreen() {
        // MainRepository has always special-cased these rather than sending them through
        // IntentProcessor, so they need the same second path here.
        assertEquals(AgenticCommandDispatcher.SCREEN_FNB,
                AgenticCommandDispatcher.directScreenFor("FnB.Show"));
    }

    @Test
    public void routesTheOtherDirectScreens() {
        assertEquals(AgenticCommandDispatcher.SCREEN_FAQ,
                AgenticCommandDispatcher.directScreenFor("hub.skills.faq"));
        assertEquals(AgenticCommandDispatcher.SCREEN_HOUSEKEEPING,
                AgenticCommandDispatcher.directScreenFor("hub.skills.housekeeping"));
        assertEquals(AgenticCommandDispatcher.SCREEN_HOTEL_INFO,
                AgenticCommandDispatcher.directScreenFor("hub.skills.hotelinfo"));
        assertEquals(AgenticCommandDispatcher.SCREEN_BOOK_SERVICE,
                AgenticCommandDispatcher.directScreenFor("hub.skills.service.book"));
        assertEquals(AgenticCommandDispatcher.SCREEN_FEEDBACK,
                AgenticCommandDispatcher.directScreenFor("hub.skills.feedback"));
        assertEquals(AgenticCommandDispatcher.SCREEN_RESTAURANTS,
                AgenticCommandDispatcher.directScreenFor("hub.skills.restaurants.book_table"));
        assertEquals(AgenticCommandDispatcher.SCREEN_TRANSLATE,
                AgenticCommandDispatcher.directScreenFor("hub.skills.translate"));
    }

    @Test
    public void sendsOrdinarySkillsThroughTheIntentRegistry() {
        // null means "no direct screen", which is the signal to use IntentProcessor.
        assertNull(AgenticCommandDispatcher.directScreenFor("hub.skills.alarms.add"));
        assertNull(AgenticCommandDispatcher.directScreenFor("hub.skills.music.play"));
        assertNull(AgenticCommandDispatcher.directScreenFor("hub.controls.system.volume.up"));
    }
}
