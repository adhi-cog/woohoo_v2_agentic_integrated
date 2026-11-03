package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.PinViewFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.di.DaggerSurveillanceComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.di.SurveillanceComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.di.SurveillanceModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class SurveillanceFragment
        extends FeatureFragment<MainActivity>
        implements SurveillanceContract.View, VideoCalling, View.OnClickListener {

    @Inject
    AnalyticsHelper helper;

    //Constants
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    @Inject
    SurveillancePresenter mSurveillancePresenter;
    private RelativeLayout dialogLayout;
    private Button btnOk, btnCancel;
    private String twilioToken;
    private String roomName;
    private boolean isInSurveillance = false;

    public SurveillanceFragment() { /*Required empty constructor*/ }

    public static SurveillanceFragment newInstance() {
        return new SurveillanceFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        SurveillanceComponent surveillanceComponent = DaggerSurveillanceComponent.builder()
                .surveillanceModule(new SurveillanceModule(this))
                .build();

        surveillanceComponent.injectPrivateLiveFeedComponent(this);
    }

    @Override
    public void bindViews(View view) {
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return mSurveillancePresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.SurveillanceOn(),  null);
        return inflater.inflate(R.layout.fragment_surveillance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        roomName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, GONE);
        intView(view);
    }

    private void intView(View v) {
        Button turnOffSurveillance = v.findViewById(R.id.turn_off_surveillance);
        dialogLayout = v.findViewById(R.id.rl_surveillance_dialog);
        btnOk = v.findViewById(R.id.button_ok);
        btnCancel = v.findViewById(R.id.button_cancel);
        turnOffSurveillance.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        View view = v.findViewById(R.id.background_layout);
        view.setOnClickListener(view1 -> {

        });
    }

    private void toggleVisibility(int status) {
        if (dialogLayout != null) {
            dialogLayout.setVisibility(status);
        }
    }


    public void initSurveillanceFeed(String token) {
        this.twilioToken = token;
        isInSurveillance = true;
        if (checkForPermission()) {
            joinRoom(twilioToken, roomName);
        }
    }

    private boolean checkForPermission() {
        //Checking for the runtime permissions for camera and microphone before connecting to twilio room
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
            return false;
        }
        return true;
    }

    private void joinRoom(String twilioToken, String roomName) {
        try {
            mSurveillancePresenter.createAudioAndVideoTracks();
            mSurveillancePresenter.connectToRoom(twilioToken, roomName);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(getParentActivity(), Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(getParentActivity(), Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED
                &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;
            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (cameraAndMicPermissionGranted) {
                joinRoom(twilioToken, roomName);
            } else {
                MethodUtils.showToastMessage(getContext(),  getString(R.string.permission_needed));
            }
        }
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getParentActivity(), Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getParentActivity(), Manifest.permission.RECORD_AUDIO)) {
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.permission_needed));
        } else {
            ActivityCompat
                    .requestPermissions(
                            getParentActivity(),
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                            CAMERA_MIC_PERMISSION_REQUEST_CODE
                    );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSurveillancePresenter.releaseVideoStreamingResources();
    }

    @Override
    public void onAPICallError(String message) {
        MethodUtils.showToastMessage(getParentActivity(), message);
    }

    @Override
    public void updateStatus() {
        isInSurveillance = false;
    }

    public void stopSurveillance() {
        mSurveillancePresenter.endCall();
        getParentActivity().startSurveillanceMode(false);
    }

    public void endSurveillanceFeed() {
        if (isInSurveillance) {
            mSurveillancePresenter.endCall();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.turn_off_surveillance:
                if (isInSurveillance) {
                    toggleVisibility(VISIBLE);
                } else {
                    getParentActivity().switchFragmentUsingAdd(PinViewFragment.newInstance(new Bundle(), PinViewFragment.TURN_OFF));
                }
                break;
            case R.id.button_ok:
                toggleVisibility(GONE);
                mSurveillancePresenter.endCall();
                getParentActivity().switchFragmentUsingAdd(PinViewFragment.newInstance(new Bundle(), PinViewFragment.TURN_OFF));
                break;
            case R.id.button_cancel:
                toggleVisibility(GONE);
                break;
        }
    }
}
