package com.sb.woohoo.utils.others;

import android.content.Context;

import com.sb.woohoo.utils.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static boolean dirExists(String dir_path) {
        boolean ret = false;
        File dir = new File(dir_path);
        if (dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    public static void deleteFolder(String dir_path) {
        File dir = new File(dir_path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }
    }

    static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean checkIfFileExists(File file) {
        return file.exists();
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    public static void writeFileOnInternalStorage(Context mContext, String sFileName, String sBody) {
        File mFile = new File(mContext.getFilesDir() + File.separator + sFileName);
        if(sFileName.equalsIgnoreCase(Constants.CRASH_LOG) && mFile.exists())
            mFile.delete();

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mFile));
            bufferedWriter.write(sBody);
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String readFileOnInternalStorage(Context mContext, String sFileName){
        String output = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                    File(mContext.getFilesDir() + File.separator + sFileName)));
            String read;
            StringBuilder builder = new StringBuilder("");

            while((read = bufferedReader.readLine()) != null){
                builder.append(read);
            }
            bufferedReader.close();
            output = builder.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }
}
