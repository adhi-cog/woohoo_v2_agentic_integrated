package com.sb.woohoo.utils;

import android.content.Context;
import android.util.Pair;

import com.sb.woohoo.custom.LoggerWrapper;
import com.twilio.video.Camera2Capturer;
import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoCapturer;

import org.webrtc.Camera2Enumerator;

import javax.inject.Inject;

/**
 * Created by ahextech on 14/3/18.
 */

public class CameraCapturerCompat {
    private CameraCapturer camera1Capturer;
    private Camera2Capturer camera2Capturer;
    private Pair<CameraCapturer.CameraSource, String> frontCameraPair;
    private Pair<CameraCapturer.CameraSource, String> backCameraPair;
    private Camera2Capturer.Listener camera2Listener;

    @Inject
    public CameraCapturerCompat() {
        LoggerWrapper.i(CameraCapturerCompat.class.getSimpleName(), "Constructor");
        initListener();
    }

    public void setCamera2Listener(Camera2Capturer.Listener camera2Listener) {
        this.camera2Listener = camera2Listener;
    }

    private void initListener() {
        camera2Listener = new Camera2Capturer.Listener() {
            @Override
            public void onFirstFrameAvailable() {
            }

            @Override
            public void onCameraSwitched(String newCameraId) {
            }

            @Override
            public void onError(Camera2Capturer.Exception camera2CapturerException) {
            }
        };
    }

    private Camera2Capturer.Listener getCamer2Listener() {
        if (camera2Listener == null) {
            initListener();
        }
        return camera2Listener;
    }

    public void initializeCameraCapturerCompat(Context context) {
        CameraCapturer.CameraSource cameraSource = getAvailableCameraSource();
        if (Camera2Capturer.isSupported(context)) {
            setCameraPairs(context);
            String cameraID = getCameraId(cameraSource);
            if (cameraID != null && !cameraID.isEmpty()) {
                camera2Capturer = new Camera2Capturer(context, cameraID, getCamer2Listener());
            }
        } else {
            camera1Capturer = new CameraCapturer(context, cameraSource);
        }
    }


    private CameraCapturer.CameraSource getAvailableCameraSource() {
        CameraCapturer.CameraSource frontCamera = CameraCapturer.CameraSource.FRONT_CAMERA;
        CameraCapturer.CameraSource rearCamera = CameraCapturer.CameraSource.BACK_CAMERA;
        return CameraCapturer.isSourceAvailable(frontCamera) ? frontCamera : rearCamera;
    }

    public CameraCapturer.CameraSource getCameraSource() {
        if (usingCamera1()) {
            return camera1Capturer.getCameraSource();
        } else {
            // FIXME : Handle NullPointerException when Camera is not available in Device.
            return getCameraSource(camera2Capturer.getCameraId());
        }
    }

    public void switchCamera() {
        if (usingCamera1()) {
            camera1Capturer.switchCamera();
        } else {
            CameraCapturer.CameraSource cameraSource = getCameraSource(camera2Capturer
                    .getCameraId());

            if (cameraSource == CameraCapturer.CameraSource.FRONT_CAMERA) {
                camera2Capturer.switchCamera(backCameraPair.second);
            } else {
                camera2Capturer.switchCamera(frontCameraPair.second);
            }
        }
    }

    /*
     * This method is required because this class is not an implementation of VideoCapturer due to
     * a shortcoming in the Video Android SDK.
     */
    public VideoCapturer getVideoCapturer() {
        if (usingCamera1()) {
            return camera1Capturer;
        } else {
            return camera2Capturer;
        }
    }

    private boolean usingCamera1() {
        return camera1Capturer != null;
    }

    private void setCameraPairs(Context context) {
        Camera2Enumerator camera2Enumerator = new Camera2Enumerator(context);
        for (String cameraId : camera2Enumerator.getDeviceNames()) {
            if (camera2Enumerator.isFrontFacing(cameraId)) {
                frontCameraPair = new Pair<>(CameraCapturer.CameraSource.FRONT_CAMERA, cameraId);
            }
            if (camera2Enumerator.isBackFacing(cameraId)) {
                backCameraPair = new Pair<>(CameraCapturer.CameraSource.BACK_CAMERA, cameraId);
            }
        }
    }

    private String getCameraId(CameraCapturer.CameraSource cameraSource) {
        String cameraID;
        if (frontCameraPair != null) {
            if (frontCameraPair.first == cameraSource) {
                cameraID = frontCameraPair.second;
            } else {
                cameraID = backCameraPair.second;
            }
        } else cameraID = "";
        return cameraID;
    }

    private CameraCapturer.CameraSource getCameraSource(String cameraId) {
        if (frontCameraPair != null) {
            if (frontCameraPair.second.equals(cameraId)) {
                return frontCameraPair.first;
            } else {
                return backCameraPair.first;
            }
        }
        return null;
    }
}


