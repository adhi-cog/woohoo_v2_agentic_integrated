package com.sb.woohoo.utils.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by tutlane on 06-01-2018.
 */

public class DbHandler {

    private File root = android.os.Environment.getExternalStorageDirectory();
    private File dir = new File(root.getAbsolutePath() + "/woohoo");
    private File file = new File(dir, "login.txt");
    private String seedValue = "woohooSeedKey";

    public void deleteFileIfthere() {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }
    }

    public String retrive() {

        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (file.exists()) {
                int length = (int) file.length();
                byte[] bytes = new byte[length];
                FileInputStream in = new FileInputStream(file);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
                String st = new String(bytes);
                String[] items = st.split("\n");

                StringBuilder value = new StringBuilder();
                value.append("" + decryptMsg(items[0]));
                value.append("\n" + decryptMsg(items[1]));
                value.append("\n" + decryptMsg(items[2]));
                value.append("\n" + decryptMsg(items[3]));
                value.append("\n" + decryptMsg(items[4]));
                value.append("\n" + decryptMsg(items[5]));
                value.append("\n" + decryptMsg(items[6]));
                value.append("\n" + decryptMsg(items[7]));
                value.append("\n" + decryptMsg(items[8]));
                value.append("\n" + decryptMsg(items[9]));
                value.append("\n" + decryptMsg(items[10]));
                return value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void insert(String UserIdType, String Image, String IsLogin, String RefreshToken,
                       String ExpiryTime, String SessionToken, String LastName, String FirstName,
                       String Email, String UserId, String Status) {
        try {

            deleteFileIfthere();

            file.createNewFile();

            FileOutputStream f = new FileOutputStream(file);
            StringBuilder value = new StringBuilder();
            value.append("" + encryptMsg(UserIdType));
            value.append("\n" + encryptMsg(Image));
            value.append("\n" + encryptMsg(IsLogin));
            value.append("\n" + encryptMsg(RefreshToken));
            value.append("\n" + encryptMsg(ExpiryTime));
            value.append("\n" + encryptMsg(SessionToken));
            value.append("\n" + encryptMsg(LastName));
            value.append("\n" + encryptMsg(FirstName));
            value.append("\n" + encryptMsg(Email));
            value.append("\n" + encryptMsg(UserId));
            value.append("\n" + encryptMsg(Status));

            f.write(value.toString().getBytes());
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String encryptMsg(String strNormalText) {
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }

    public String decryptMsg(String strEncryptedText) {
        String strDecryptedText = "";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }
}