package com.sb.woohoo.ui.main;

import android.os.AsyncTask;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.callbacks.InternetCheckCallback;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class InternetCheckTask extends AsyncTask<ConnectivityHelper.Address, Boolean, Boolean> {

    private InternetCheckCallback internetCheckCallback;
    private int server;

    public InternetCheckTask(InternetCheckCallback internetCheckCallback) {
        this.internetCheckCallback = internetCheckCallback;
    }

    @Override
    protected Boolean doInBackground(ConnectivityHelper.Address... address) {
        server = address[0].getServer();
        return checkSocket(address[0].getIp(), address[0].getPort());
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        internetCheckCallback.isInternetAvailable(aBoolean, server);
    }

    private boolean checkSocket(String ip, int port) {
        try {
            int timeOut = 5000;
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            socket.connect(socketAddress, timeOut);
            socket.close();

            return true;
        } catch (IOException e) {
            LoggerWrapper.e(MainActivity.class.getSimpleName(), e.getMessage());
        }
        return false;
    }
}
