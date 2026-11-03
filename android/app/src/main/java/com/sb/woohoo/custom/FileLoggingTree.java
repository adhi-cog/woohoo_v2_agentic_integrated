package com.sb.woohoo.custom;

import android.content.Context;

import timber.log.Timber;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLoggingTree extends Timber.Tree {
    private final File logFile;
    private final long maxSizeBytes;
    private final Context context;

    public FileLoggingTree(Context context, File directory, String filePrefix, long maxSizeBytes) {
        this.context = context;
        this.logFile = new File(directory, filePrefix + "log.txt");
        this.maxSizeBytes = maxSizeBytes;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            // Rotate file if it exceeds max size
            if (logFile.exists() && logFile.length() > maxSizeBytes) {
                logFile.renameTo(new File(context.getFilesDir(), "log_old.txt"));
                logFile.createNewFile();
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
            String logLine = String.format("%s [%s] %s%n", timestamp, tag, message);

            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(logLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*

// Initialize in your Application or Activity
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree()); // For LogCat
        Timber.plant(new FileLoggingTree(this, getFilesDir(), "", logFileMaxSizeBytes));
        Timber.d("TAG", "This goes to both LogCat and file");
    }
}*/
