package com.sb.woohoo.ui.main.fragments.settings.cameracontrol;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sb.woohoo.custom.LoggerWrapper;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private android.hardware.Camera mCamera;

    public CameraPreview(CameraControlFragment context, android.hardware.Camera camera) {
        super(context.getContext());
        this.mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            LoggerWrapper.d("Camera View", e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) return;

        try {
            mCamera.startPreview();
        } catch (Exception e) {
            LoggerWrapper.d("Camera View", e.getMessage());
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
