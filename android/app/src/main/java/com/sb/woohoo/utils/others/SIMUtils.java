package com.sb.woohoo.utils.others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.fragments.settings.sim.SimSettingsFragment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Inject this class to use sim features.
 *
 * @author Ashish Yadav.
 */
public class SIMUtils {

    private Context context;
    private List<SubscriptionInfo> activeSubscriptionInfoList;
    private TelephonyManager telephonyManager;

    @SuppressLint({"MissingPermission", "NewApi"})
    public SIMUtils(Context context) {
        this.context = context;
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Use this method to check whether sim is present or not.
     */
    public boolean isSimPresent() {
        return activeSubscriptionInfoList != null && !activeSubscriptionInfoList.isEmpty();
    }

    /**
     * Use this method to get {@link SubscriptionInfo} i.e. SIM details
     * Get displayName,carrierName etc. data from SIM using {@link SubscriptionInfo}
     * Retrieving the first element from the list because the device supports single sim.
     */
    public SubscriptionInfo getSubscriptionInfo() {
        if (isSimPresent()) {
            return activeSubscriptionInfoList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Method to check mobile data on/off.
     */
    public boolean isMobileDataOn() {
        try {
            Method getMobileDataEnabledMethod = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");

            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyManager);
        } catch (Exception ex) {
            LoggerWrapper.e(SimSettingsFragment.class.getSimpleName(), "Error getting mobile data state");
        }
        return false;
    }

    /**
     * Method to turn mobile data on/off.
     */
    public void toggleMobileData(boolean enable) throws IOException, SecurityException {
        StringBuilder command = new StringBuilder();
        command.append("su -c ");
        command.append("service call phone ");
        command.append(getTransactionCode()).append(" ");
        if (Build.VERSION.SDK_INT >= 22) {
            SubscriptionManager manager = SubscriptionManager.from(context);
            int id = 0;
            if (manager.getActiveSubscriptionInfoCount() > 0)
                id = manager.getActiveSubscriptionInfoList().get(0).getSubscriptionId();
            command.append("i32 ");
            command.append(String.valueOf(id)).append(" ");
        }
        command.append("i32 ");
        command.append(enable ? "1" : "0");
        command.append("\n");
        Runtime.getRuntime().exec(command.toString());
    }

    private String getTransactionCode() {
        try {
            Class telephonyManagerClass = null;
            if (telephonyManager != null) {
                telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            }
            Method getITelephonyMethod = null;
            if (telephonyManagerClass != null) {
                getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
            }

            if (getITelephonyMethod != null) {
                Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
                Class ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
                Class stub = ITelephonyClass.getDeclaringClass();
                Field field = null;
                if (stub != null) {
                    field = stub.getDeclaredField("TRANSACTION_setDataEnabled");
                }
                if (field != null) {
                    field.setAccessible(true);
                }
                if (field != null) {
                    return String.valueOf(field.getInt(null));
                }
            }
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT >= 22)
                return "86";
            else if (Build.VERSION.SDK_INT == 21)
                return "83";
        }
        return "";
    }
}