package com.sb.woohoo.ui.main.agentic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sb.woohoo.data.network.WooHooApiCall;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import retrofit2.http.Header;

/**
 * The hosted gateway's edge refuses a turn that arrives without `x-pilot-key`, and the app
 * reports that refusal to the guest as "I could not reach the assistant just then" - the
 * same sentence it uses for an unreachable server.
 *
 * So dropping this header does not break a build or fail a contract test against a local
 * gateway, which asks for no key. It breaks every hub in the field and looks like a network
 * problem. Reflection is a blunt instrument, but the alternative is no guard at all on a
 * change whose failure mode is a plausible-looking wrong answer.
 */
public class AgenticRequestHeaderTest {

    private static final String KEY_HEADER = "x-pilot-key";

    private static Method declared(String name) {
        for (Method m : WooHooApiCall.class.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        fail("WooHooApiCall no longer declares " + name + "()");
        return null;
    }

    private static void assertCarriesKeyHeader(String methodName) {
        Method m = declared(methodName);
        assertNotNull(m);
        boolean found = false;
        for (Annotation[] perParameter : m.getParameterAnnotations()) {
            for (Annotation a : perParameter) {
                if (a instanceof Header && KEY_HEADER.equals(((Header) a).value())) {
                    found = true;
                }
            }
        }
        assertTrue(
                methodName + "() must declare a @Header(\"" + KEY_HEADER + "\") parameter, or "
                        + "every turn against the hosted gateway is refused with 403 and the "
                        + "guest is told the assistant is unreachable",
                found);
    }

    @Test
    public void theTurnCallCarriesTheRequestKeyHeader() {
        assertCarriesKeyHeader("agenticTurn");
    }

    @Test
    public void theAckCallCarriesTheRequestKeyHeader() {
        // The ack is fire-and-forget, so losing it is quieter still: the guest is already
        // answered and only the operations view of what the device did goes dark.
        assertCarriesKeyHeader("agenticCommandAck");
    }

    @Test
    public void theHeaderNameMatchesWhatTheEdgeChecks() {
        // nginx reads $http_x_pilot_key, which is this name lowercased with dashes. A rename
        // on either side is a silent 403, so the literal is pinned here on purpose.
        assertTrue("header name must stay lowercase and dash-separated",
                KEY_HEADER.equals(KEY_HEADER.toLowerCase()) && KEY_HEADER.contains("-"));
    }
}
