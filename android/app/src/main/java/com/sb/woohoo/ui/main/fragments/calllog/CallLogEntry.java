package com.sb.woohoo.ui.main.fragments.calllog;

public class CallLogEntry {
    public static final String DIRECTION_INCOMING = "Incoming";
    public static final String DIRECTION_OUTGOING = "Outgoing";

    private String number;
    private String direction;
    private long timestamp;

    public CallLogEntry() {
    }

    public CallLogEntry(String number, String direction, long timestamp) {
        this.number = number;
        this.direction = direction;
        this.timestamp = timestamp;
    }

    public String getNumber() {
        return number;
    }

    public String getDirection() {
        return direction;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
