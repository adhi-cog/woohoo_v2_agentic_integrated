package com.sb.woohoo.ui.main.helpers;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sb.woohoo.ui.base.BaseHelper;

import javax.inject.Inject;

public class DeviceHelper extends BaseHelper {
    private static final String TAG = DeviceHelper.class.getSimpleName();

    private Gson gson;
    private JsonParser jsonParser;
    private DeviceListener mListener;

    @Inject
    public DeviceHelper(Gson gson, JsonParser jsonParser) {
        LoggerWrapper.i(DeviceHelper.class.getSimpleName(), "Constructor");
        this.gson = gson;
        this.jsonParser = jsonParser;
    }

    public void setListener(DeviceListener listener) {
        this.mListener = listener;
    }

    /**
     * Emit Light object to Socket
     */
//    public void onLightStateEmitSocket ( Light light ) {
//
////        socketHelper = new SocketHelper ( socket );
//
//        String userId = WooHooApp.getSharedPrefsHelper ().get ( SharedPrefsKey.PREF_KEY_USER_ID, "" );
//        String deviceId = WooHooApp.getSharedPrefsHelper ().get ( SharedPrefsKey.PREF_KEY_DEVICE_ID, "" );
//
//        light.setHubId ( deviceId );
//        light.setFrom ( "device" );
//        light.setUserId ( userId );
//        String object = gson.toJson ( light );
//        JsonObject socketPutJson = jsonParser.parse ( object ).getAsJsonObject();
//        try {
//            JSONObject jsonObject = new JSONObject(String.valueOf(socketPutJson));
//            mListener.emitLightState ( jsonObject );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void onHelperCleanUp() {
        super.onHelperCleanUp();
        gson = null;
        jsonParser = null;
        mListener = null;
    }

    public interface DeviceListener {
        void emitLightState(JsonObject object);
    }
}
