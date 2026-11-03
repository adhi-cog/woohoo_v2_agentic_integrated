package com.sb.woohoo.ui.main.pojo.agentic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/** Body for POST /v1/device-commands/{commandId}/ack. */
public class AgenticAckRequest {

    /** applied | failed | expired | unsupported. */
    @SerializedName("status")
    @Expose
    private final String status;

    @SerializedName("detail")
    @Expose
    private final String detail;

    @SerializedName("at")
    @Expose
    private final String at;

    public AgenticAckRequest(String status, String detail, String at) {
        this.status = status;
        this.detail = detail;
        this.at = at;
    }
}
