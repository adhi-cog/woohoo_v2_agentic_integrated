package com.sb.woohoo.ui.main.pojo.agentic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Body for POST /v1/turn.
 *
 * `deviceId` addresses the device; it is not proof of anything. The platform resolves it to
 * a room and verifies it against the client certificate serial the TLS terminator forwards,
 * so a tampered deviceId gets a 403 rather than someone else's room.
 */
public class AgenticTurnRequest {

    @SerializedName("deviceId")
    @Expose
    private final String deviceId;

    @SerializedName("transcript")
    @Expose
    private final String transcript;

    @SerializedName("asrConfidence")
    @Expose
    private final double asrConfidence;

    public AgenticTurnRequest(String deviceId, String transcript, double asrConfidence) {
        this.deviceId = deviceId;
        this.transcript = transcript;
        this.asrConfidence = asrConfidence;
    }

    public String getTranscript() {
        return transcript;
    }
}
