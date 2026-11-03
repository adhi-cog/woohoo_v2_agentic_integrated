package com.sb.woohoo.utils.others;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.main.helpers.WakeWordHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.sb.woohoo.ui.main.fragments.settings.wakewordswitch.WakeWordSwitchFragment.GLOBAL_WAKEWORD;

public class AssetUtils {

    private static final String EnrollOutFile = "enrolled.snsr";
    private static final String TAG = WakeWordHelper.class.getSimpleName();
    private static final String EnrollSubDir = "/enroll/";

    public static File getOutDir(Context context) {
        return new File(context.getFilesDir(), EnrollSubDir);
    }

    public static File getModelFile(Context context) {
//        File file;
//        file = new File(context.getExternalFilesDir(null), EnrollOutFile);
//        if (FileUtils.checkIfFileExists(file)) {
//            return file;
//        } else {
//            return copyAsset(context);
//        }
        LoggerWrapper.d("GLOBAL_WAKEWORD", String.valueOf(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD)));
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD) == GLOBAL_WAKEWORD) {
            return copyAsset(context);
        } else {
            File enrollOutDir = getOutDir(context);
            File enrollOutFile = new File(enrollOutDir, EnrollOutFile);
            LoggerWrapper.d("enrollOutFile", enrollOutFile.getAbsolutePath());
            if (FileUtils.checkIfFileExists(enrollOutFile)) {
                return enrollOutFile;
            } else {
                return copyAsset(context);
            }
        }

    }

    public static File getModelExists(Context context) {
//        File file;
//        file = new File(context.getExternalFilesDir(null), EnrollOutFile);
//        if (FileUtils.checkIfFileExists(file)) {
//            return file;
//        } else {
//            return copyAsset(context);
//        }
        LoggerWrapper.d("GLOBAL_WAKEWORD", String.valueOf(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD)));
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD) == GLOBAL_WAKEWORD) {
            return copyAsset(context);
        } else {
            File enrollOutDir = getOutDir(context);
            File enrollOutFile = new File(enrollOutDir, EnrollOutFile);
            LoggerWrapper.d("enrollOutFile", enrollOutFile.getAbsolutePath());
            if (FileUtils.checkIfFileExists(enrollOutFile)) {
                return enrollOutFile;
            } else {
                return null;
            }
        }

    }

    private static File copyAsset(Context context) {
        AssetManager assetManager = context.getAssets();
        File outFile;
        OutputStream out = null;
        InputStream in = null;
        try {
            in = assetManager.open(EnrollOutFile);
            outFile = new File(context.getExternalFilesDir(null), EnrollOutFile);
            out = new FileOutputStream(outFile);
            FileUtils.copyFile(in, out);
        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.getMessage());
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LoggerWrapper.e(TAG, e.getMessage());
            }
        }
        return outFile;
    }
}
