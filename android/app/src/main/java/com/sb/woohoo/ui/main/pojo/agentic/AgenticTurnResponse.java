package com.sb.woohoo.ui.main.pojo.agentic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Response from the agentic gateway's POST /v1/turn.
 *
 * Two arrays arrive and they mean opposite things, which is the single most important thing
 * to get right when reading this class:
 *
 *   calls[]          work the platform ALREADY DID. A report. Dispatching any of it locally
 *                    fires the vendor command a second time, so the guest's light goes off
 *                    and straight back on. Never pass these to IntentProcessor.
 *
 *   deviceCommands[] work the platform CANNOT do, because only this device can: alarms,
 *                    volume, media, screens. These are the ones to dispatch.
 *
 * The legacy NLP path had a single intentAction that the app was expected to execute, and
 * carrying that assumption across is how the duplicate-execution bug gets introduced.
 */
public class AgenticTurnResponse implements Serializable {

    @SerializedName("turnId")
    @Expose
    private String turnId;

    /** What the guest hears. Already phrased to match what actually happened. */
    @SerializedName("speech")
    @Expose
    private String speech;

    /** Optional screen hint from the platform, independent of deviceCommands. */
    @SerializedName("screen")
    @Expose
    private String screen;

    /** fast | agentic | clarify | confirm | degraded. */
    @SerializedName("lane")
    @Expose
    private String lane;

    /** True when the platform is waiting for the guest to confirm on the next turn. */
    @SerializedName("awaitingConfirmation")
    @Expose
    private boolean awaitingConfirmation;

    @SerializedName("escalated")
    @Expose
    private boolean escalated;

    /** Already executed server-side. Report only. */
    @SerializedName("calls")
    @Expose
    private List<AgenticCall> calls;

    /** For this device to execute and acknowledge. */
    @SerializedName("deviceCommands")
    @Expose
    private List<AgenticDeviceCommand> deviceCommands;

    public String getTurnId() {
        return turnId;
    }

    public String getSpeech() {
        return speech;
    }

    public String getScreen() {
        return screen;
    }

    public String getLane() {
        return lane;
    }

    public boolean isAwaitingConfirmation() {
        return awaitingConfirmation;
    }

    public boolean isEscalated() {
        return escalated;
    }

    /** Never null, so callers do not have to guard every iteration. */
    public List<AgenticCall> getCalls() {
        return calls == null ? new ArrayList<>() : calls;
    }

    public List<AgenticDeviceCommand> getDeviceCommands() {
        return deviceCommands == null ? new ArrayList<>() : deviceCommands;
    }

    /**
     * Whether anything the platform attempted actually failed.
     *
     * Speech is phrased for the failure already, so this is not used to decide what to say.
     * It is used to decide what to log, because a turn that looks fine to the guest and
     * failed at the head-end is the case that otherwise goes unnoticed.
     */
    public boolean hasFailedCall() {
        for (AgenticCall call : getCalls()) {
            if (!call.isOk()) {
                return true;
            }
        }
        return false;
    }
}
