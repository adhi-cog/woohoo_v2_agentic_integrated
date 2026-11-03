package com.sb.woohoo.ui.main.agentic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticDeviceCommand;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnRequest;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The app's own Retrofit interface against a real gateway.
 *
 * The dispatcher tests cover decisions from hand-written JSON. This covers the thing those
 * cannot: that the JSON the platform actually produces deserializes into these classes. A
 * field renamed on the server is invisible to every other test in this repository and shows
 * up in a room as an alarm that silently never gets set.
 *
 * Skipped, not failed, when no gateway is reachable. A developer without one running should
 * not see a red build, but the moment one is up this is the test that catches drift:
 *
 *   cd agentic && npm run gateway
 */
public class AgenticGatewayContractTest {

    private static final String BASE_URL = System.getProperty(
            "agentic.baseUrl", "http://localhost:8443");
    /** Seeded by the gateway's default property. */
    private static final String DEVICE_ID = System.getProperty("agentic.deviceId", "d_412a");

    private static boolean gatewayUp;

    private WooHooApiCall api;

    @BeforeClass
    public static void probeGateway() {
        gatewayUp = reachable(BASE_URL + "/health");
    }

    @Before
    public void setUp() {
        assumeTrue("no agentic gateway at " + BASE_URL + "; start it with: npm run gateway", gatewayUp);
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL + "/")
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(WooHooApiCall.class);
    }

    private static boolean reachable(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(1500);
            c.setReadTimeout(1500);
            c.setRequestMethod("GET");
            int code = c.getResponseCode();
            c.disconnect();
            return code == 200;
        } catch (IOException unreachable) {
            return false;
        }
    }

    private AgenticTurnResponse turn(String transcript) {
        return api.agenticTurn(
                        BASE_URL + "/v1/turn",
                        new AgenticTurnRequest(DEVICE_ID, transcript, 0.97d))
                .blockingGet();
    }

    @Test
    public void aTurnDeserializesIntoTheAppsOwnClasses() {
        AgenticTurnResponse r = turn("turn on the bedroom lights");
        assertNotNull("no response body", r);
        assertNotNull("speech is what the guest hears; it cannot be absent", r.getSpeech());
        assertFalse(r.getSpeech().trim().isEmpty());
        assertNotNull(r.getTurnId());
        assertNotNull(r.getLane());
    }

    @Test
    public void roomAutomationProducesNoWorkForThisDevice() {
        // The duplicate-execution guarantee, verified against the real server rather than
        // asserted in a comment. The platform drove the light itself; if a device command
        // came back for it, the hub would drive it a second time.
        AgenticTurnResponse r = turn("turn on the bedroom lights");
        assertTrue(
                "lighting must not come back as device work: " + r.getDeviceCommands(),
                r.getDeviceCommands().isEmpty());
        assertFalse("the platform should have executed something", r.getCalls().isEmpty());
    }

    @Test
    public void deviceOnlyWorkComesBackAsACommandThisAppCanDispatch() {
        AgenticTurnResponse r = turn("turn up the volume");
        assertFalse("expected a device command for volume", r.getDeviceCommands().isEmpty());

        AgenticDeviceCommand c = r.getDeviceCommands().get(0);
        assertEquals("hub.controls.system.volume.up", c.getIntent());
        assertNotNull("without an id the hub cannot deduplicate", c.getCommandId());
        assertNotNull("without an expiry a stale command runs late", c.getExpiresAt());

        // The decisive check: the dispatcher accepts what the server actually sent.
        AgenticCommandDispatcher dispatcher = new AgenticCommandDispatcher();
        assertTrue(
                "the dispatcher rejected a live command from the gateway",
                dispatcher.evaluate(c, System.currentTimeMillis()).shouldDispatch());
    }

    @Test
    public void slotsSurviveTheRoundTrip() {
        AgenticTurnResponse r = turn("set an alarm for 7 am");
        assertFalse("expected an alarm command", r.getDeviceCommands().isEmpty());
        AgenticDeviceCommand c = r.getDeviceCommands().get(0);
        assertEquals("hub.skills.alarms.add", c.getIntent());
        assertEquals("07:00", String.valueOf(c.getSlots().get("time")));
    }

    @Test
    public void everyDeviceCommandResolvesToSomethingThisAppCanRun() {
        // A command naming an intent no fragment handles is the failure mode the closed
        // vocabulary exists to prevent, so check it against a live server too.
        String[] utterances = {
                "turn up the volume", "set an alarm for 7 am", "play some music",
                "what time is it", "open the faq",
        };
        for (String u : utterances) {
            for (AgenticDeviceCommand c : turn(u).getDeviceCommands()) {
                assertFalse(
                        "\"" + u + "\" produced a room-device intent: " + c.getIntent(),
                        AgenticCommandDispatcher.isForbidden(c.getIntent()));
                boolean knownScreen = AgenticCommandDispatcher.directScreenFor(c.getIntent()) != null;
                boolean looksLikeHubIntent = c.getIntent().startsWith("hub.")
                        || c.getIntent().startsWith("FnB.");
                assertTrue(
                        "\"" + u + "\" produced an unroutable intent: " + c.getIntent(),
                        knownScreen || looksLikeHubIntent);
            }
        }
    }

    @Test
    public void anUnknownDeviceIsRefusedRatherThanServed() {
        try {
            api.agenticTurn(
                            BASE_URL + "/v1/turn",
                            new AgenticTurnRequest("d_not_enrolled", "turn on the lights", 0.9d))
                    .blockingGet();
            org.junit.Assert.fail("a device the catalog does not know must not get a turn");
        } catch (RuntimeException expected) {
            // Retrofit wraps the 404. The point is that it is not a 200.
            assertTrue(expected.getMessage(), expected.getMessage().contains("404"));
        }
    }
}
