package com.sb.woohoo.utils.others;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.custom.LoggerWrapper;
import com.spotify.sdk.android.player.Connectivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class MethodUtils {

    public static void hideSoftInputKeyboard(View view, Context context) {
        if (view != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void hideSoftInputKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(),
                    0);
        }
    }

    /**
     * Shows {@link Toast} with a Message.
     *
     * @param context Context from where this method is called.
     * @param message Message to be displayed by the {@link Toast}.
     * @author Ritwik Jamuar
     */
    public static void showToastMessage(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
                return Connectivity.fromNetworkType(activeNetwork.getType());
            else
                return Connectivity.OFFLINE;
        }
        return Connectivity.OFFLINE;
    }


    /**
     * Converts Fahrenheit to Celcius.
     *
     * @param fahrenheit Degree in Fahrenheit.
     * @return Degree in Celcius.
     * @author Ritwik Jamuar.
     */
    public static double convertFahrenheitToCelcius(double fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    public static String formatTime(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        long minutes = seconds / 60;

        minutes = minutes % 60;
        seconds = seconds % 60;

        String min = String.valueOf(minutes);
        String sec = String.valueOf(seconds);

        if (seconds < 10) {
            sec = "0" + seconds;
        }
        if (minutes < 10) {
            min = "0" + minutes;
        }

        return min + ":" + sec;
    }

    public static String formatTimeInHrs(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        hours = hours % 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        String hrs = String.valueOf(minutes);
        String min = String.valueOf(minutes);
        String sec = String.valueOf(seconds);

        if (seconds < 10) {
            sec = "0" + seconds;
        }
        if (minutes < 10) {
            min = "0" + minutes;
        }
        if (hours < 10) {
            hrs = "0" + hours;
        }

        return hrs + ":" + min + ":" + sec;
    }

    public static String formatTime(int seconds) {
        String min, sec;

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        if (minutes < 10) min = "0" + minutes;
        else min = "" + minutes;

        if (remainingSeconds < 10) sec = "0" + remainingSeconds;
        else sec = "" + remainingSeconds;

        return min + ":" + sec;
    }

    public static String setWordWithCaps(String wordToCaps) {
        StringBuilder stringBuilder = new StringBuilder(wordToCaps);
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        return stringBuilder.toString();
    }


    public static String getTodayDate() {
        Date c = Calendar.getInstance(TimeZone.getDefault()).getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        return df.format(c);
    }

    /**
     * Converts MilliSeconds to Seconds.
     *
     * @param milliSeconds Time in MilliSeconds.
     * @return {@code int} value denoting Seconds.
     * @author Ritwik Jamuar.
     */
    public static int convertMilliSecondsToSeconds(long milliSeconds) {
        return (int) milliSeconds / 1000;
    }

    /**
     * Gets the Activity's window and set decor's view to hide System UI.
     *
     * @param activity Instance of Activity.
     */
    public static void setActivityFullScreen(Activity activity) {
        Window window = activity.getWindow();
        if (window != null) {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    public static void setActivityFullScreen(Window window) {
        if (window != null) {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

    }

    public static boolean isResource(Context context, int resId) {
        if (context != null) {
            try {
                return context.getResources().getResourceName(resId) != null;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else return false;
    }

    public static void printLog(Class c, String message) {
        LoggerWrapper.e(c.getSimpleName(), message);
    }


    // Setting margins to dialog in landscape ..
    public static Dialog setDialogMargins(Dialog dialog)
    {
        Window window = dialog.getWindow();
        if ( window == null ) return dialog;
        Context context = dialog.getContext();

        // set dialog to fullscreen
        RelativeLayout root = new RelativeLayout( context );
        root.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( root );
        // set right and bottom margin implicitly by calculating width and height of dialog
        Point displaySize = getDisplayDimensions( context );
        int width = displaySize.x - (displaySize.x)/2;
        int height = displaySize.y;
        window.setLayout( width, height );
        window.setGravity( Gravity.CENTER );

        return dialog;
    }

    public static Dialog setAlertDialogMargins(Dialog dialog)
    {
        Window window = dialog.getWindow();
        if ( window == null ) return dialog;
        Context context = dialog.getContext();

        // set dialog to fullscreen
        RelativeLayout root = new RelativeLayout( context );
        root.setLayoutParams( new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT ) );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( root );
        // set right and bottom margin implicitly by calculating width and height of dialog
        Point displaySize = getDisplayDimensions( context );
        if (dialog.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            int width = displaySize.x - (displaySize.x)/2;
            int height = displaySize.y;
            window.setLayout( width, height );
        }else{
            int width = (int) (displaySize.x * 0.9);
            int height = (int) (displaySize.y * 0.8);
            window.setLayout(width, height );
        }
        window.setGravity( Gravity.CENTER );

        return dialog;
    }

    @NonNull
    public static Point getDisplayDimensions(Context context )
    {
        WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics( metrics );
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics( metrics );
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight( context );
        int navigationBarHeight = getNavigationBarHeight( context );
        int heightDelta = physicalHeight - screenHeight;
        if ( heightDelta == 0 || heightDelta == navigationBarHeight )
        {
            screenHeight -= statusBarHeight;
            screenHeight -= navigationBarHeight;
        }

        return new Point( screenWidth, screenHeight );
    }

    public static int getStatusBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }

    public static int getNavigationBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }

    public static int getColorFromTheme(Context context, int resId){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(resId, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
    }

    public static void copyTextToClipBoard(Context context, String text, String message) {
        if (!text.isEmpty()) {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("key", text);
            clipboardManager.setPrimaryClip(clipData);
            if (message != null)
                showToastMessage(context, message);
        }
    }

    //To test its valid float..
    public static boolean isFloat(String aVal) {
        try {
            Float.parseFloat(aVal);
            return true;
        } catch(NumberFormatException e) {
            // Not float
            return false;
        }
    }

    public static String getVersionNameAndVersionCode(){
        return "v"+ BuildConfig.VERSION_CODE + "_" + BuildConfig.VERSION_NAME;
    }

    public static String getVersionCode(){
        return "v"+ BuildConfig.VERSION_CODE;
    }

    public static boolean hasSystemPermission(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();

            // Get package information
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SHARED_LIBRARY_FILES);

            // Access the sharedUserId
            String sharedUserId = packageInfo.sharedUserId;

            // Check if the app has the specified system-level permission
            return sharedUserId != null && "android.uid.system".equals(sharedUserId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean goToSleep(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("goToSleep", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
            return true;

        } catch (Exception e) {
        }
        return false;
    }

    public static int flagForPendingIntent(){
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return flags;
    }
}