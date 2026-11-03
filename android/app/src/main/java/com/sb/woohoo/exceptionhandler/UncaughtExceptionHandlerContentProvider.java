package com.sb.woohoo.exceptionhandler;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*References :
* https://github.com/firebase/firebase-android-sdk/issues/2005
* https://proandroiddev.com/hide-your-crashes-gracefully-and-still-report-them-9b1c85b25875*/
public class UncaughtExceptionHandlerContentProvider extends ContentProvider {
    private static final String TAG = "UncaughtExceptionCP";

    @Override
    public boolean onCreate() {
        Application application = (Application) getContext().getApplicationContext();
        boolean isDebuggable = (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (!isDebuggable) {
            Thread.setDefaultUncaughtExceptionHandler(
                    new AppExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), application)
            );
        } else {
            Log.d(TAG, "Skipping AppExceptionHandler for debuggable build");
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
