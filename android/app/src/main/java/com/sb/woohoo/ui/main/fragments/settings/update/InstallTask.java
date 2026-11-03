package com.sb.woohoo.ui.main.fragments.settings.update;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.main.sip.compatibility.Compatibility;
import com.sb.woohoo.utils.db.DbHandler;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.sb.woohoo.utils.Constants.SERVICE_APK_NAME;
import static com.sb.woohoo.utils.Constants.SERVICE_CLASS_PATH;
import static com.sb.woohoo.utils.Constants.SERVICE_PACKAGE_NAME;
import static com.sileria.android.Resource.getAssets;

public class InstallTask extends AsyncTask<String, Integer, Boolean> {


    private InstallCallback installCallback;
    private String filepath;
    private Context context;

    private Boolean status = false;

    public InstallTask(Context context, InstallCallback installCallback, String filepath) {
        this.installCallback = installCallback;
        this.filepath = filepath;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        installCallback.startInstallation();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();

        DbHandler dbHandler = new DbHandler();
        dbHandler.insert(helper.get(SharedPrefsKey.PREF_USER_ID_TYPE, ""),
                helper.get(SharedPrefsKey.PREF_IMAGE, ""),
                helper.get(SharedPrefsKey.PREF_IS_LOGIN, ""),
                helper.get(SharedPrefsKey.PREF_KEY_REFRESH_TOKEN, ""),
                helper.get(SharedPrefsKey.PREF_EXPIRE_TIME, ""),
                helper.get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, ""),
                helper.get(SharedPrefsKey.PREF_KEY_LAST_NAME, ""),
                helper.get(SharedPrefsKey.PREF_KEY_FIRST_NAME, ""),
                helper.get(SharedPrefsKey.PREF_KEY_EMAIL, ""),
                helper.get(SharedPrefsKey.PREF_KEY_USER_ID, ""),
                helper.get(SharedPrefsKey.PREF_KEY_STATUS, ""));
        return install();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean) {
            installCallback.onInstall(aBoolean);
        }
    }

    private boolean install() {
        status = false;
        try {

            LoggerWrapper.i(InstallTask.class.getSimpleName(), "**** woohoo monitor app uninstall start ");
            AsyncTaskUnInstall asyncTaskUnInstall = new AsyncTaskUnInstall();
            asyncTaskUnInstall.execute(SERVICE_PACKAGE_NAME);
            status = true;


        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    private void openWoohooMonitor() {
        LoggerWrapper.i(InstallTask.class.getSimpleName(), "**** woohoo monitor app opening  " + filepath);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_PATH));
        intent.putExtra("PATH", filepath);
        Compatibility.startService(context,intent);
        installCallback.onInstall(true);
    }

    private class AsyncTaskUnInstall extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                Process reboot = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(reboot.getOutputStream());
                os.writeBytes("pm uninstall " + strings[0] + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                reboot.waitFor();
            } catch (Exception e) {
                LoggerWrapper.e("TAG", "*** woohoo monitor error1 " + e.getMessage());
                return false;
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                LoggerWrapper.d(InstallTask.class.getSimpleName(), "*** woohoo monitor app uninstall sucess ");

                AssetManager assetManager = getAssets();
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(SERVICE_APK_NAME);
                    out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + SERVICE_APK_NAME);

                    byte[] buffer = new byte[1024];

                    int read;
                    while ((read = in.read(buffer)) != -1) {

                        out.write(buffer, 0, read);

                    }
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                    LoggerWrapper.i(InstallTask.class.getSimpleName(), "*** woohoo monitor app install start ");

                    AsyncTaskInstall asyncTaskInstall = new AsyncTaskInstall();
                    asyncTaskInstall.execute(Environment.getExternalStorageDirectory().getPath() + "/" + SERVICE_APK_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                LoggerWrapper.e(InstallTask.class.getSimpleName(), "*** woohoo monitor app uninstall unsucefull ");
                installCallback.onInstall(aBoolean);
            }
        }
    }

    private class AsyncTaskInstall extends AsyncTask<String, String, Boolean> {


        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Process reboot = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(reboot.getOutputStream());
                os.writeBytes("pm install " + strings[0] + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                reboot.waitFor();
            } catch (Exception e) {
                LoggerWrapper.e(InstallTask.class.getSimpleName(), "*** woohoo monitor error2 " + e.getMessage());
                return false;
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                LoggerWrapper.i(InstallTask.class.getSimpleName(), "*** woohoo monitor app installl sucess " + aBoolean);
                openWoohooMonitor();

            } else {
                installCallback.onInstall(aBoolean);
            }

        }
    }
}
