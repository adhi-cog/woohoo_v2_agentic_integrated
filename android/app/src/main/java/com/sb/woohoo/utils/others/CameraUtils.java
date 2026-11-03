package com.sb.woohoo.utils.others;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.helpers.HandlerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

public class CameraUtils {

    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    public static final int MOTOR_SPEED = 2;
    private  final int ROTATION_FACTOR = 5;  // degrees
    private  final int MOTOR_SPEED_MAX = 1;

    private int rotationDirection;
    private int rotationSpeed;
    private boolean isContinuous = false;
    private boolean isRemote = false;

    //    private HandlerThread cameraHandlerThread;
    private HandlerHelper cameraRotationHandler;
    private File cameraFile;

    private Runnable cameraRotationRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                OutputStream os = new FileOutputStream(cameraFile);
                os.write(String.format("%d %d\n", isRemote ? MOTOR_SPEED_MAX : rotationSpeed, rotationDirection == DIR_RIGHT ? +ROTATION_FACTOR : -ROTATION_FACTOR).getBytes());
                os.close();
            } catch (IOException e) {
                LoggerWrapper.e(MethodUtils.class.getSimpleName(), e.getMessage());
            }

            if (isContinuous) {
                // In millis
                int ROTATION_DELAY_LOCAL = 100;
                // In millis
                int ROTATION_DELAY_REMOTE = 30;
                cameraRotationHandler.getBackgroundHandler().postDelayed(cameraRotationRunnable, isRemote ? ROTATION_DELAY_REMOTE : ROTATION_DELAY_LOCAL);
            }
        }
    };

    @Inject
    public CameraUtils(HandlerHelper manager) {
        LoggerWrapper.i(CameraUtils.class.getSimpleName(), "Constructor");
        cameraRotationHandler = manager;
        cameraFilesSetup();
    }


    private void cameraFilesSetup() {
        try {
            File cameraFileDirectory = new File("/sys/exgpio/");
            String cameraFileName = "motor";
            if (!cameraFileDirectory.exists()) {
                cameraFileDirectory.mkdirs();
            }
            cameraFile = new File(cameraFileDirectory, cameraFileName);
            if (!cameraFile.exists()) {
                cameraFile.createNewFile();
            }
        } catch (IOException fileException) {
            LoggerWrapper.e(CameraUtils.class.getSimpleName(), "Camera file open error!");
        }
    }

    public void rotateCamera(int direction, int speed) {
        this.rotationSpeed = speed;
        this.rotationDirection = direction;
        this.isContinuous = false;
        cameraRotationHandler.getBackgroundHandler().post(cameraRotationRunnable);
    }

    public void rotateCameraContinuous(int direction, int speed, boolean isRemoteControl) {
        this.rotationDirection = direction;
        this.rotationSpeed = speed;
        this.isContinuous = true;
        this.isRemote = isRemoteControl;
        cameraRotationHandler.getBackgroundHandler().post(cameraRotationRunnable);
    }

    public void stopCameraRotation() {
        cameraRotationHandler.getBackgroundHandler().removeCallbacks(cameraRotationRunnable);
    }

    public void cleanUp() {
        cameraRotationHandler.getBackgroundHandler().removeCallbacks(cameraRotationRunnable);
//        cameraRotationHandler.getBackgroundHandler().getLooper().getThread().interrupt();
//        cameraRotationHandler.getBackgroundHandler().getLooper().quitSafely();
    }
}
