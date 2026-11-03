package com.sb.woohoo.utils.woohoblutooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothA2DPRequester implements BluetoothProfile.ServiceListener {
    private Callback mCallback;
    private BluetoothDevice device;
    private boolean connect;
    private String TAG = BluetoothA2DPRequester.class.getSimpleName();
    private int profile;
    private BluetoothA2dp bluetoothA2dp;
    private BluetoothAdapter adapter;

    /**
     * Creates a new instance of an A2DP Proxy requester with the
     * callback that should receive the proxy once it is acquired
     */
    public BluetoothA2DPRequester() {
    }

    /**
     * Start an asynchronous request to acquire the A2DP proxy. The callback
     * will be notified when the proxy is acquired
     * @param c the context used to obtain the proxy
     * @param adapter the BluetoothAdapter that should receive the request for proxy
     */
    public void request (Context c, BluetoothAdapter adapter, BluetoothDevice device) {
        this.device = device;
        this.adapter = adapter;
        adapter.getProfileProxy(c, this, BluetoothProfile.A2DP);
    }

    public void disconnectDevice () {
        Method connect = getDisConnectMethod();
        //If either is null, just return. The errors have already been logged
        if (connect == null || device == null || adapter ==  null) {
            return;
        }
        try {
            connect.setAccessible(true);
            connect.invoke(bluetoothA2dp, device);
        } catch (InvocationTargetException ex) {
            Log.e(TAG, "Unable to invoke connect(BluetoothDevice) method on proxy. " + ex.toString());
        } catch (IllegalAccessException ex) {
            Log.e(TAG, "Illegal Access! " + ex.toString());
        }
    }

    @Override
    public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
        profile = i;
        bluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
        if(!isConnected(device))
            onA2DPProxyReceived(i, (BluetoothA2dp) bluetoothProfile,device);
        else
            disconnectDevice();
    }

    @Override
    public void onServiceDisconnected(int i) {
        //It's a one-off connection attempt; we don't care about the disconnection event.
    }

    public static interface Callback {
        public void onA2DPProxyReceived (int profile, BluetoothA2dp proxy, BluetoothDevice device);
    }

    public void onA2DPProxyReceived (int profile, BluetoothA2dp proxy, BluetoothDevice device) {
        Method connect = getConnectMethod();
        //If either is null, just return. The errors have already been logged
        if (connect == null || device == null) {
            return;
        }
        try {
            connect.setAccessible(true);
            connect.invoke(proxy, device);
        } catch (InvocationTargetException ex) {
            Log.e(TAG, "Unable to invoke connect(BluetoothDevice) method on proxy. " + ex.toString());
        } catch (IllegalAccessException ex) {
            Log.e(TAG, "Illegal Access! " + ex.toString());
        }
    }

    /**
     * Wrapper around some reflection code to get the hidden 'connect()' method
     * @return the connect(BluetoothDevice) method, or null if it could not be found
     */
    private Method getConnectMethod () {
        try {
            return BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
        } catch (NoSuchMethodException ex) {
            Log.e(TAG, "Unable to find connect(BluetoothDevice) method in BluetoothA2dp proxy.");
            return null;
        }
    }

    private Method getDisConnectMethod () {
        try {
            return BluetoothA2dp.class.getDeclaredMethod("disconnect", BluetoothDevice.class);
        } catch (NoSuchMethodException ex) {
            Log.e(TAG, "Unable to find disconnect(BluetoothDevice) method in BluetoothA2dp proxy.");
            return null;
        }
    }

    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected(BluetoothDevice device) {
        // ref: https://stackoverflow.com/questions/4715865/how-can-i-programmatically-tell-if-a-bluetooth-device-is-connected
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}