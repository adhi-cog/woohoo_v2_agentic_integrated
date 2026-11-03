package com.sb.woohoo.ui.main.helpers;

import android.os.AsyncTask;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.InternetCheckTask;
import com.sb.woohoo.ui.main.callbacks.InternetCheckCallback;

import javax.inject.Inject;

import static com.sb.woohoo.utils.Constants.INTERNET_CHECK_SERVER;

public class ConnectivityHelper extends BaseHelper {
    // Constants.
    private static final String TAG = ConnectivityHelper.class.getSimpleName();
    private static final int GOOGLE_SERVER = 300;
    private static final int WOOHOO_SERVER = 301;
    public static final String GOOGLE_ADDRESS = "8.8.8.8";
    public static final int GOOGLE_PORT = 53;
    private static final String WOOHOO_ADDRESS = INTERNET_CHECK_SERVER;
    private static final int WOOHOO_PORT = 443;
    // AsyncTasks.
    private InternetCheckTask internetCheckTask;
    // Listeners.
    private ConnectivityHandlerCallback callback;
    private InternetCheckCallback internetCheckCallback = new InternetCheckCallback() {
        @Override
        public void isInternetAvailable(boolean connectivityStatus, int server) {
            if (connectivityStatus && server == GOOGLE_SERVER) {
                if(internetCheckTask!=null){
                    internetCheckTask.cancel(true);
                }
                internetCheckTask = null;
                internetCheckTask = new InternetCheckTask(internetCheckCallback);
                Address address = new Address(WOOHOO_ADDRESS, WOOHOO_PORT, WOOHOO_SERVER);
                internetCheckTask.execute(address);
            } else if (!connectivityStatus && server == GOOGLE_SERVER) {
                callback.internetDown();
            } else if (!connectivityStatus && server == WOOHOO_SERVER) {
                callback.serverDown();
            } else {
                callback.internetUp();
            }
        }
    };

    @Inject
    public ConnectivityHelper() {
        LoggerWrapper.i(TAG, "Constructor");
    }

    public void setListener(ConnectivityHandlerCallback callback) {
        this.callback = callback;
    }

    public void checkInternet() {
        internetCheckTask = new InternetCheckTask(internetCheckCallback);
        //Checking Google Server for internet connection
        Address address = new Address(GOOGLE_ADDRESS, GOOGLE_PORT, GOOGLE_SERVER);
        internetCheckTask.execute(address);
    }

    @Override
    public void onHelperCleanUp() {
        if (internetCheckTask != null && internetCheckTask.getStatus() != AsyncTask.Status.FINISHED) {
            internetCheckTask.cancel(true);
        }
        internetCheckTask = null;
        callback = null;
    }

    public interface ConnectivityHandlerCallback {
        void internetDown();

        void serverDown();

        void internetUp();
    }

    public class Address {
        String ip;
        int port;
        int server;

        Address(String ip, int port, int server) {
            this.ip = ip;
            this.port = port;
            this.server = server;
        }

        public int getServer() {
            return server;
        }

        public void setServer(int server) {
            this.server = server;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

//        public void setPort(int port) {
//            this.port = port;
//        }
    }
}