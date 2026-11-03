package com.sb.woohoo.ui.main.fragments.settings.update;

import android.os.AsyncTask;
import android.os.Environment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.utils.others.FileUtils;
import com.sb.woohoo.utils.others.MethodUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 12/5/17.
 */

public class UpdateTask extends AsyncTask<String, Integer, Integer> {

    private int versionCode;
    private UpdateCallback updateCallback;

    public UpdateTask(int versionCode, UpdateCallback updateCallback) {
        this.versionCode = versionCode;
        this.updateCallback = updateCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        updateCallback.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        updateCallback.onProgressUpdate(values[0], values[1]);
        MethodUtils.printLog(UpdateTask.class, "Downloading " + values[0] + " File Size " + values[1]);
    }

    @Override
    protected Integer doInBackground(String... sUrl) {

        MethodUtils.printLog(UpdateTask.class, "doInBackground(String... sUrl)");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                MethodUtils.printLog(UpdateTask.class, "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return 0;
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();

            //Write the path only not file name
            String dir_path = Environment.getExternalStorageDirectory() + "/sb";

            if (FileUtils.dirExists(dir_path)) {
                FileUtils.deleteFolder(dir_path);
            }

            File directory = new File(dir_path);
            directory.mkdirs();

            String fileName = String.valueOf(versionCode) + ".apk";//23.apk
            MethodUtils.printLog(UpdateTask.class, fileName);
            MethodUtils.printLog(UpdateTask.class, dir_path + "/" + fileName);
            output = new FileOutputStream(dir_path + "/" + fileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return 0;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength), fileLength);
                } // only if total length is known

                output.write(data, 0, count);
            }
        } catch (Exception e) {
            MethodUtils.printLog(UpdateTask.class, e.getMessage());
            return 0;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                MethodUtils.printLog(UpdateTask.class, e.getMessage());
            }
            if (connection != null)
                connection.disconnect();
        }
        return versionCode;
    }

    @Override
    protected void onPostExecute(Integer s) {
        String dir_path = Environment.getExternalStorageDirectory() + "/sb";
        String fileName = String.valueOf(versionCode) + ".apk";
        if (s == 0) {
            //Delete unfinished..
            MethodUtils.printLog(UpdateTask.class, "Unfinished Download");
            FileUtils.deleteFolder(dir_path);
            updateCallback.onUpdateFailed();
        } else {
            //install the downloaded apk..
            SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();
            helper.put(SharedPrefsKey.PREF_DOWNLOAD_STATUS, true);

            MethodUtils.printLog(UpdateTask.class, "Download Success");
            updateCallback.onUpdateSuccess(dir_path + "/" + fileName);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        String dir_path = Environment.getExternalStorageDirectory() + "/sb";
//        String fileName = String.valueOf(versionCode) + ".apk";
        FileUtils.deleteFolder(dir_path);
        updateCallback.onUpdateCancelled();
    }
}
