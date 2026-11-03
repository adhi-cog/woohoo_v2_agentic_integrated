package com.sb.woohoo.ui.main.fragments.settings.cameracontrol;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentCameraControlBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.IntentInfo;


/**
 * Shows and manages Camera control of user.
 *
 * @author Pankaj
 */
// TODO : Remove this Fragment.
public class CameraControlFragment extends FeatureFragment<MainActivity> {

    private final int CAMERA_ANGLE = 0;

    //FIXME No DI setup
    /*CameraUtils cameraUtils = new CameraUtils();*/
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_CAMERA;

    private Camera mCamera;
    private FragmentCameraControlBinding binding;
//    private int cameraAngle = CameraUtils.ROATATION_FACTOR;
////    private boolean stopRotation = true;

    public static CameraControlFragment newInstance() {
        return new CameraControlFragment();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return CameraControlFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraControlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

    }

    @Override
    public void bindViews(View view) {

        binding.interceptorLayout.setContext(getParentActivity());

        initViews();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraInitialization();
        binding.cameraControlBackButton.setOnClickListener(view1 -> onBackCameraControl());
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    private void initViews() {
        binding.cameraControlLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        /*cameraUtils.rotateCameraContinuous(CameraUtils.DIR_LEFT, CameraUtils.MOTOR_SPEED, false);*/
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        /*cameraUtils.stopCameraRotation();*/
                        break;

                }

                return false;
            }
        });


        binding.cameraControlRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        /*cameraUtils.rotateCameraContinuous(CameraUtils.DIR_RIGHT, CameraUtils.MOTOR_SPEED, false);*/
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        /*cameraUtils.stopCameraRotation();*/
                        break;

                }

                return false;
            }
        });

        ((View) binding.cameraPreview.getParent()).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void cameraInitialization() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            mCamera.setDisplayOrientation(CAMERA_ANGLE);
            CameraPreview mPreview = new CameraPreview(this, mCamera);
            binding.cameraPreview.addView(mPreview);
        } else {
            LoggerWrapper.e(CameraControlFragment.class.getSimpleName(), "cameraInitialization: camera is null");
        }
    }

    @SuppressWarnings("deprecation")
    public Camera getCameraInstance() {
        Camera c = null;
        int cameraCount = Camera.getNumberOfCameras();
        try {
            c = Camera.open();
            int cameraIndex = 0;
            while (c == null || cameraIndex < cameraCount) {
                c = Camera.open(cameraIndex++);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return c;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.release();
        }
        /*cameraUtils.cleanUp();*/
    }

    void onBackCameraControl() {
        getActivity().onBackPressed();
    }
}
