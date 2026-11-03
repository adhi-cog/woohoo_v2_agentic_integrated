package com.sb.woohoo.ui.main.fragments.calllog;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class CallLogStorage {
    private static final int MAX_ENTRIES = 100;
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<CallLogEntry>>() {}.getType();

    private CallLogStorage() {
    }

    public static List<CallLogEntry> getEntries() {
        String json = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_CALL_LOGS, "");
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        List<CallLogEntry> entries = GSON.fromJson(json, LIST_TYPE);
        return entries == null ? new ArrayList<>() : entries;
    }

    public static void logIncoming(String address) {
        addEntry(buildEntry(address, CallLogEntry.DIRECTION_INCOMING));
    }

    public static void logOutgoing(String address) {
        addEntry(buildEntry(address, CallLogEntry.DIRECTION_OUTGOING));
    }

    public static void clear() {
        WooHooApp.getSharedPrefsHelper().clear(SharedPrefsKey.PREF_KEY_CALL_LOGS);
    }

    private static void addEntry(CallLogEntry entry) {
        if (entry == null) {
            return;
        }
        List<CallLogEntry> entries = new ArrayList<>(getEntries());
        entries.add(0, entry);
        if (entries.size() > MAX_ENTRIES) {
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }
        save(entries);
    }

    private static void save(List<CallLogEntry> entries) {
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_CALL_LOGS, GSON.toJson(entries));
    }

    private static CallLogEntry buildEntry(String address, String direction) {
        String number = extractNumber(address);
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        return new CallLogEntry(number, direction, System.currentTimeMillis());
    }

    private static String extractNumber(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        String value = address.trim();
        if (value.startsWith("sip:")) {
            value = value.substring(4);
        }
        int atIndex = value.indexOf('@');
        if (atIndex >= 0) {
            value = value.substring(0, atIndex);
        }
        int paramIndex = value.indexOf(';');
        if (paramIndex >= 0) {
            value = value.substring(0, paramIndex);
        }
        return value.trim();
    }
}
