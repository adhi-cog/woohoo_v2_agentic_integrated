package com.sb.woohoo.custom;


import android.util.Log;

import com.sb.woohoo.utils.logger.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import timber.log.Timber;

public abstract class LoggerWrapper {

        private static Set loggers = new HashSet<Logger>();


        public static void plusAssign(Logger logger) {
            loggers.add(logger);
        }

       public static void minusAssign(Logger logger) {
            loggers.remove(logger);
        }

    public static int v(final String TAG, final String message) {
        Log.v(TAG, message);
        try {
            Timber.v(TAG, message); // Logs to all trees
            return  0; // 0 for success, -1 for failure

        } catch (RuntimeException e) {
            return -1;
        }
    }

    public static int d(final String TAG, final String message) {
        Log.d(TAG, message);
        try {
            Iterator iterator = loggers.iterator();
            while(iterator.hasNext()) {
                Logger logger = (Logger)iterator.next();
                logger.d(TAG, message);
            }
            Timber.d(TAG, message);
            return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public static int i(final String TAG, final String message) {
        Log.i(TAG, message);
        try {
           /* Iterator iterator = loggers.iterator();
            while(iterator.hasNext()) {
                Logger logger = (Logger)iterator.next();
                logger.d(TAG, message);
            }*/
             Timber.i(TAG, message);
             return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public static int w(final String TAG, final String message) {
        Log.w(TAG, message);
        Timber.w(TAG, message);
        return 0;
    }

    public static int e(final String TAG, final String message) {
        Log.e(TAG, message);
        try {

            Iterator iterator = loggers.iterator();
            while(iterator.hasNext()) {
                Logger logger = (Logger)iterator.next();
                logger.e(TAG, message);
            }
            Timber.e(TAG, message);
            return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }
}
