package com.sb.woohoo.ui.main.pojo.agentic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Work for this device to do, handed over by the platform.
 *
 * `intent` is one of the app's own names, so it resolves through IntentProcessor exactly as
 * an NLP intentAction used to. That is deliberate: it means alarms, music, video and the
 * rest keep working unchanged when the voice engine is replaced.
 *
 * `commandId` is an idempotency key. The same command can arrive twice, because the platform
 * mints the id from the turn and a retried turn repeats it. Executing twice must be safe, so
 * the dispatcher keeps a short memory of ids it has already run.
 */
public class AgenticDeviceCommand implements Serializable {

    @SerializedName("commandId")
    @Expose
    private String commandId;

    @SerializedName("intent")
    @Expose
    private String intent;

    @SerializedName("slots")
    @Expose
    private HashMap<String, Object> slots;

    /** Spoken alongside this command, when the command itself is the answer. Usually absent. */
    @SerializedName("speech")
    @Expose
    private String speech;

    /** ISO-8601. After this the command is stale and must be dropped rather than run. */
    @SerializedName("expiresAt")
    @Expose
    private String expiresAt;

    public String getCommandId() {
        return commandId;
    }

    public String getIntent() {
        return intent;
    }

    public HashMap<String, Object> getSlots() {
        return slots == null ? new HashMap<>() : slots;
    }

    public String getSpeech() {
        return speech;
    }

    public String getExpiresAt() {
        return expiresAt;
    }
}
