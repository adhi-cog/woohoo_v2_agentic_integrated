package com.sb.woohoo.utils.woohoblutooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothBReceiver";
    private Callback mCallback;
    public BluetoothBroadcastReceiver(){}

    public BluetoothBroadcastReceiver (Callback callback) {
        mCallback = callback;
    }

    /**
     * Convenience method to register a new instance of this receiver with the
     * necessary IntentFilter, which will notify the callback upon a successful
     * connection or error case.
     * @param callback the callback that should be notified on success or failure
     *                 of Bluetooth being enabled
     * @param c the context from which to register the receiver
     */
    public static void register (Callback callback, Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            c.registerReceiver(new BluetoothBroadcastReceiver(callback), getFilter(),
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            c.registerReceiver(new BluetoothBroadcastReceiver(callback), getFilter());
        }
    }

    private static IntentFilter getFilter () {
        return new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    public void onReceive (Context context, Intent intent) {
        //This is a State Change event, get the state extra, falling back to ERROR
        //if it isn't there (which shouldn't happen)
        String state = intent.getAction();
        LoggerWrapper.e(TAG, " onReceive Error "+state);
        switch (state) {
            case "android.bluetooth.device.action.ACL_CONNECTED":
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                fireOnBluetoothConnected(device);
                break;
            case "android.bluetooth.device.action.ACL_DISCONNECTED":
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                fireOnBluetoothDisconnected(device);
                break;
            default:
                fireOnBluetoothError();
                break;
        }
    }

    /**
     * Convenience method to do a checked unregistration of a broadcast receiver. Occasionally
     * you can get into a state where the receiver was already unregistered, throwing an
     * IllegalArgumentException. This method just swallows that exception and logs the error.
     * @param c the context from which to unregister the receiver
     */
    public static void safeUnregisterReceiver (Context c, BluetoothBroadcastReceiver receiver) {
        try {
            c.unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Tried unregister BluetoothBReceiver, not registered.");
        }
    }

    private void fireOnBluetoothConnected (BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBluetoothConnected(device);
        }
    }

    private void fireOnBluetoothDisconnected (BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBluetoothDisconnected(device);
        }
    }

    private void fireOnBluetoothError () {
        if (mCallback != null) {
            mCallback.onBluetoothError();
        }
    }

    public static interface Callback {
        public void onBluetoothConnected(BluetoothDevice device);
        public void onBluetoothDisconnected(BluetoothDevice device);
        public void onBluetoothError();
    }
}