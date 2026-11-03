package com.sb.woohoo.ui.main.pojo.agentic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * One tool call the platform already executed. A report, never an instruction.
 *
 * `ok` is the field that matters. The platform phrases speech to match the real outcome, so
 * the app does not decide what to say from this, but a false here is the difference between
 * a command that reached the room and one that did not, and it is the only place that shows.
 */
public class AgenticCall implements Serializable {

    @SerializedName("tool")
    @Expose
    private String tool;

    @SerializedName("tier")
    @Expose
    private String tier;

    /** Policy decision: allow, deny, confirm. */
    @SerializedName("verdict")
    @Expose
    private String verdict;

    @SerializedName("ok")
    @Expose
    private boolean ok;

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("latencyMs")
    @Expose
    private Integer latencyMs;

    public String getTool() {
        return tool;
    }

    public String getTier() {
        return tier;
    }

    public String getVerdict() {
        return verdict;
    }

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }
}
