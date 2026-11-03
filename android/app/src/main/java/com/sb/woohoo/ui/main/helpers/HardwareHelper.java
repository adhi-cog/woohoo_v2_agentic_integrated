package com.sb.woohoo.ui.main.helpers;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.KeyEvent;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.callbacks.BrightnessCallback;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.VolumeControl;
import com.sb.woohoo.utils.others.CameraUtils;
import com.twilio.video.Camera2Capturer;
import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoCapturer;

import javax.inject.Inject;

/**
 * Handles all the callbacks related to Hardware.
 */
@SuppressWarnings("DanglingJavadoc")
public class HardwareHelper extends BaseHelper {
    // Constants.
    public static final String TAG = HardwareHelper.class.getSimpleName();
    /**
     * Use this when no Volume Stream is specified.
     */
    public static final int DEFAULT_VOLUME_STREAM = -1;
    // Hardware Components.
    private CameraUtils mCameraUtils;
    private CameraCapturerCompat mCameraCaptureCompat;
    private VolumeControl mVolumeControl;
    private BrightnessHelper mBrightnessHelper;

    @Inject
    public HardwareHelper(
            CameraUtils cameraUtils,
            CameraCapturerCompat cameraCapturerCompat,
            VolumeControl volumeControl,
            BrightnessHelper brightnessHelper) {
        LoggerWrapper.i(HardwareHelper.class.getSimpleName(), "Constructor");
        this.mCameraUtils = cameraUtils;
        this.mCameraCaptureCompat = cameraCapturerCompat;
        this.mVolumeControl = volumeControl;
        this.mBrightnessHelper = brightnessHelper;
    }

    /************************************************* Common Methods *************************************************/

    /**
     * Sets the {@link Camera2Capturer.Listener} to {@link CameraCapturerCompat}.
     *
     * @param listener Instance of {@link Camera2Capturer.Listener}.
     */
    public void setCameraCaptureCompatListener(Camera2Capturer.Listener listener) {
        this.mCameraCaptureCompat.setCamera2Listener(listener);
    }

    /**
     * Sets the {@link VolumeControl.VolumeControlCallbacks} to {@link VolumeControl}.
     *
     * @param callbacks Instance of {@link VolumeControl.VolumeControlCallbacks}.
     */
    public void setVolumeControlCallbacks(VolumeControl.VolumeControlCallbacks callbacks) {
        if (this.mVolumeControl != null) {
            this.mVolumeControl.setVolumeControlCallbacks(callbacks);
        }
    }

    /**
     * Sets the {@link BrightnessCallback} to {@link BrightnessHelper}.
     *
     * @param callback Instance of {@link BrightnessCallback}.
     */
    public void setBrightnessChangeObserver(BrightnessCallback callback) {
        this.mBrightnessHelper.setBrightnessChangeObserver(callback);
    }

    // TODO : Add this switch case in MainActivity.

    /**
     * To be called when some key is pressed, which is notified in
     * {@link android.app.Activity}'s {@code onKey()} callback
     *
     * @param keyCode Key Code present in {@link KeyEvent}.
     * @param event   Instance of {@link KeyEvent}.
     */
    public void onHardwareKeyPressed(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() == 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    printLog("Volume UP Pressed!");
                }
                break;

                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    printLog("Volume DOWN Pressed!");
                }
                break;
            }
        }
    }

    public void onVolumeUpPressed() {
        mVolumeControl.onVolumeButtonPressed();
    }

    public void onVolumeDownPressed() {
        // TODO : Update UI after this call.
//        mVolumeControl.setVolume(mVolumeControl.getVolume() - 1);
    }

    public void initializeComponents(Context context) {
        mCameraCaptureCompat.initializeCameraCapturerCompat(context);
    }

    private void printLog(String message) {
        LoggerWrapper.i(TAG, message);
    }

    /****************************************** {@link CameraUtils} Methods *******************************************/

    /**
     * Rotate Camera either continuously or for certain rotational angle.
     *
     * @param direction       Can either be {@code CameraUtils.DIR_LEFT} or {@code CameraUtils.DIR_RIGHT}.
     * @param speed           Can be either {@code CameraUtils.MOTOR_SPEED} or {@code CameraUtils.MOTOR_SPEED_MAX}.
     * @param isRemoteControl Can be {@code true} when Motor is set to be controlled remotely, else {@code false}.
     * @param isContinuous    Can be {@code true} when Motor is set to rotate continuously in given direction,
     *                        else {@code false}.
     */
    public void rotateCamera(int direction, int speed, boolean isRemoteControl, boolean isContinuous) {
        if (isContinuous) {
            mCameraUtils.rotateCameraContinuous(direction, speed, isRemoteControl);
        } else {
            mCameraUtils.rotateCamera(direction, speed);
        }
    }

    /**
     * Stops Camera Motor to rotate.
     */
    public void stopCameraRotation() {
        mCameraUtils.stopCameraRotation();
    }

    /************************************* {@link CameraCapturerCompat} Methods ***************************************/

    /**
     * Get Camera Source.
     *
     * @return Instance of {@link CameraCapturer.CameraSource}
     */
    public CameraCapturer.CameraSource getCameraSource() {
        return mCameraCaptureCompat.getCameraSource();
    }

    /**
     * Get Video Capturer from source.
     *
     * @return Instance of {@link VideoCapturer}
     */
    public VideoCapturer getVideoCapturer() {
        return mCameraCaptureCompat.getVideoCapturer();
    }

    /**
     * Switches between Primary and Secondary Camera
     */
    public void switchCamera() {
        mCameraCaptureCompat.switchCamera();
    }

    /***************************************** {@link VolumeControl} Methods ******************************************/

    /**
     * Change the current active stream with new stream provided.
     *
     * @param player     {@link MediaPlayer} to change stream with.
     * @param streamType Type of stream to which audio shall be routed. Must be value from
     *                   {@link android.media.AudioManager}.
     */
    public void setCurrentActiveStream(MediaPlayer player, int streamType) {
        mVolumeControl.setCurrentActiveStream(player, streamType);
    }

    /**
     * Gains the focus of Audio in given Audio Stream.
     *
     * @param streamType Type of stream to which audio shall gain focus on. Must be value from
     *                   {@link android.media.AudioManager}.
     */
    public void gainAudioFocus(int streamType) {
        mVolumeControl.gainAudioFocus(streamType);
    }

    /**
     * Decreases volume by some percentage in the current stream to give audio mute effect.
     */
    public void duckVolume(int audioStream) {
        mVolumeControl.duckVolume(audioStream);
    }

    /**
     * Raises the volume of audio playing in current stream.
     *
     * @param step Value by which audio volume level shall be increased.
     */
    public void raiseVolume(int step) {
        mVolumeControl.raiseVolume(step);
    }

    /**
     * Restores the volume of Audio from given or current Audio Stream.
     */
    public void restoreVolume(int audioStream) {
        mVolumeControl.restoreVolume(audioStream);
    }

    /**
     * Controls the System Volume on the basis of Intent received from NLP Engine.
     *
     * @param intentInfo {@link IntentInfo}'s instance containing information about Intent.
     */
    public void controlSystemVolume(IntentInfo intentInfo) {
        mVolumeControl.controlSystemVolume(intentInfo);
    }

    /**
     * Mutes the audio of Current Audio Stream.
     *
     * @param muteState {@code true} when you want to Mute Audio, else {@code false}.
     */
    public void muteCurrentStream(boolean muteState) {
        mVolumeControl.muteCurrentStream(muteState);
    }

    /**
     * Gets the current volume level of Audio.
     *
     * @return Volume Level of Current Audio Stream.
     */
    public int getVolume() {
        return mVolumeControl.getVolume();
    }

    /**
     * Sets the current volume level of Audio.
     *
     * @param volume Level you want to set to current Audio Stream.
     */
    public void setVolume(int volume) {
        mVolumeControl.setVolume(volume);
    }

    /**
     * Gets the maximum volume level of Audio.
     *
     * @return Maximum Volume Level of Current Audio Stream.
     */
    public int getMaxVolume() {
        return mVolumeControl.getMaxVolume();
    }

    public void registerVolumeObserver(int audioStreamType) {
        mVolumeControl.setVolumeObserver(audioStreamType);
        mVolumeControl.registerVolumeObserver();
    }

    public void unRegisterVolumeObserver() {
        mVolumeControl.unregisterVolumeObserver();
    }

    /**************************************** {@link BrightnessHelper} Methods ***************************************/

    /**
     * Get the current Brightness level of Device.
     *
     * @return Level of Brightness. Value ranges between 0 and 255.
     */
    public int getBrightness() {
        return mBrightnessHelper.getBrightness();
    }

    /**
     * Set the Brightness of Device
     *
     * @param brightness Level of Brightness. Value ranges between 0 and 255.
     */
    public void setBrightness(int brightness) {
        mBrightnessHelper.setBrightness(brightness);
    }

    @Override
    public void onHelperCleanUp() {
        mVolumeControl.unregisterVolumeObserver();
        if (mCameraUtils != null) {
            mCameraUtils.cleanUp();
        }
        mCameraUtils = null;
        mCameraCaptureCompat = null;
        mVolumeControl = null;
        mBrightnessHelper.releaseResources();
        mBrightnessHelper = null;
    }
}