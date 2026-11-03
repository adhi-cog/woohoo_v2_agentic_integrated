package com.sb.woohoo.di;

import android.content.Context;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.TokenRefreshData;
import com.sb.woohoo.data.TokenRefreshModel;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

public class TokenAuthenticator implements okhttp3.Authenticator {
    private Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) {
        LoggerWrapper.i(TokenAuthenticator.class.getSimpleName(), "authenticate() : ");
        return tokenAuthenticatorCall(response);
    }

    private synchronized Request tokenAuthenticatorCall(Response response) {
        SharedPrefsHelper sharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
        String sessionToken = sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, "");
        String refreshToken = sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_REFRESH_TOKEN, "");
        String userId = sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_USER_ID, "");
        String deviceId = sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        String userTypeId = getUserIdType(sharedPrefsHelper.get(SharedPrefsKey.PREF_USER_ID_TYPE, ""));

        if (response.code() != 200) {
            JsonObject jsonObject = buildJson(refreshToken, userId, deviceId, userTypeId);
            JsonObject responseJSON = makeApiRequest(jsonObject);
            TokenRefreshData tokenRefreshData = parseJSON(responseJSON);

            String newSessionToken = null;
            String newRefreshToken = null;
            if (tokenRefreshData != null) {
                newSessionToken = tokenRefreshData.getSessionToken();
                newRefreshToken = tokenRefreshData.getRefreshToken();
            }

            if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                sharedPrefsHelper.put(SharedPrefsKey.PREF_KEY_REFRESH_TOKEN, newRefreshToken);
            }

            if (newSessionToken != null && !newSessionToken.isEmpty()) {

                sharedPrefsHelper.put(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, newSessionToken);

                return response.request().newBuilder()
                        .header("Authorization", newSessionToken)
                        .build();
            } else {
                return response.request().newBuilder()
                        .header("Authorization", sessionToken)
                        .build();
            }
        } else {
            return response.request().newBuilder()
                    .header("Authorization", sessionToken)
                    .build();
        }
    }

    private String getUserIdType(String userIdType) {
        if (userIdType.equalsIgnoreCase("domainCustomer")) {
            return "3";
        } else if (userIdType.equalsIgnoreCase("woohooCustomer")) {
            return "4";
        } else
            return "";
    }

    private JsonObject buildJson(String refreshToken, String userId, String deviceId, String userTypeId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("refreshToken", refreshToken);
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("userTypeId", userTypeId);

        return jsonObject;
    }

    private JsonObject makeApiRequest(JsonObject json) {
        JsonObject jsonObjectResp = null;
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(BuildConfig.WOOHOO_BASE_URL + "user/refreshToken")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            String networkResp = null;
            if (responseBody != null) {
                networkResp = responseBody.string();
            }
            if (networkResp != null && !networkResp.isEmpty()) {
                return parseJSONStringToJSONObject(networkResp);
            }
        } catch (Exception ex) {
            jsonObjectResp = new JsonObject();
            jsonObjectResp.addProperty("status", 400);
            jsonObjectResp.addProperty("message", ex.getMessage());
            jsonObjectResp.addProperty("data", "");
            jsonObjectResp.addProperty("error", "");
        }
        return jsonObjectResp;
    }

    private JsonObject parseJSONStringToJSONObject(final String responseString) {
        return (new JsonParser()).parse(responseString).getAsJsonObject();
    }

    private TokenRefreshData parseJSON(JsonObject responseJson) {
        Gson gson = new Gson();
        TokenRefreshModel responseModel = gson.fromJson(responseJson, TokenRefreshModel.class);
        if (responseModel.getStatus() == 200) {
            return responseModel.getData();
        } else {
            return null;
        }
    }
}
