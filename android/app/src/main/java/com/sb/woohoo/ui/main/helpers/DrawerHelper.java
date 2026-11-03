package com.sb.woohoo.ui.main.helpers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.sileria.android.Kit.getSystemService;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import android.widget.SeekBar;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.ActivityHomeBinding;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sileria.android.view.SlidingTray;

import javax.inject.Inject;

public class DrawerHelper extends BaseHelper
        implements SeekBar.OnSeekBarChangeListener, SlidingTray.OnDrawerScrollListener {
    private final ActivityHomeBinding binding;

    // Listeners.
    private DrawerCallbacks drawerCallbacks;

    @Inject
    public DrawerHelper(MainActivity activity) {
        LoggerWrapper.i(DrawerHelper.class.getSimpleName(), "Constructor");
        this.binding = activity.binding;
    }

    private AudioManager mgr;

    public void setListener(DrawerCallbacks callbacks) {
        this.drawerCallbacks = callbacks;
    }

    public void initDrawer(int currentBrightness) {
        binding.content.seekBarBrightness.setMax(255);
        binding.content.seekBarBrightness.setKeyProgressIncrement(1);
        binding.content.seekBarBrightness.setProgress(currentBrightness);
        binding.content.seekBarBrightness.setOnSeekBarChangeListener(this);
    }
    public void initAudioManager(){
        mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public void setDefaultSeekBarValue() {
        int maximumVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int currentVolume = mgr.getStreamVolume(AudioManager.STREAM_SYSTEM);
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_IS_FIRST_TIME, true)) {
            currentVolume = (int) (maximumVolume*0.4);
        }

        binding.content.seekBarVolume.setOnSeekBarChangeListener(null);
        binding.content.seekBarVolume.setMax(maximumVolume);
        binding.content.seekBarVolume.setProgress(currentVolume);


        maximumVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_RING);
        currentVolume = mgr.getStreamVolume(AudioManager.STREAM_RING);
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_IS_FIRST_TIME, true)) {
            currentVolume = (int) (maximumVolume*0.4);
        }

        binding.content.seekBarVolumeTwo.setOnSeekBarChangeListener(null);
        binding.content.seekBarVolumeTwo.setMax(maximumVolume);
        binding.content.seekBarVolumeTwo.setProgress(currentVolume);


        maximumVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_IS_FIRST_TIME, true)) {
            currentVolume = (int) (maximumVolume*0.4);
        }

        binding.content.seekBarVolumeThree.setOnSeekBarChangeListener(null);
        binding.content.seekBarVolumeThree.setMax(maximumVolume);
        binding.content.seekBarVolumeThree.setProgress(currentVolume);

        maximumVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        currentVolume = mgr.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_IS_FIRST_TIME, true)) {
            currentVolume = (int) (maximumVolume*0.4);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_IS_FIRST_TIME, false);
        }

        binding.content.seekBarVolumeFour.setOnSeekBarChangeListener(null);
        binding.content.seekBarVolumeFour.setMax(maximumVolume);
        binding.content.seekBarVolumeFour.setProgress(currentVolume);


        setSeekbarListener();
    }

    private void setSeekbarListener() {
        binding.content.seekBarVolume.setOnSeekBarChangeListener(this);
        binding.content.seekBarVolumeTwo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress,
                                          boolean fromUser) {
                mgr.setStreamVolume(AudioManager.STREAM_RING, progress,
                        AudioManager.FLAG_PLAY_SOUND);
            }

            public void onStartTrackingTouch(SeekBar bar) {
                // no-op
            }

            public void onStopTrackingTouch(SeekBar bar) {
                // no-op
            }
        });

        binding.content.seekBarVolumeThree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress,
                                          boolean fromUser) {
                mgr.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
                        AudioManager.FLAG_PLAY_SOUND);
            }

            public void onStartTrackingTouch(SeekBar bar) {
                // no-op
            }

            public void onStopTrackingTouch(SeekBar bar) {
                // no-op
            }
        });

        binding.content.seekBarVolumeFour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress,
                                          boolean fromUser) {
                mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress,
                        AudioManager.FLAG_PLAY_SOUND);
            }

            public void onStartTrackingTouch(SeekBar bar) {
                // no-op
            }

            public void onStopTrackingTouch(SeekBar bar) {
                // no-op
            }
        });
    }


    public void initListener() {

        binding.slidingTray.setOnDrawerOpenListener(() -> {
            binding.slidingTray.unlock();
            closeSlidingTrayBottom();
            drawerCallbacks.onDrawerOpen();

        });
        binding.slidingTray.setOnDrawerCloseListener(() -> {
            binding.slidingTray.unlock();
            drawerCallbacks.onDrawerClose();
        });

        binding.slidingTray.setOnDrawerScrollListener(new SlidingTray.OnDrawerScrollListener() {
            @Override
            public void onScrollStarted() {
                closeSlidingTrayBottom();
            }

            @Override
            public void onScrollEnded() {
                binding.slidingTray.unlock();
            }
        });
    }

    @Override
    public void onScrollStarted() {
        binding.slidingTray.lock();
        closeSlidingTray();
    }

    @Override
    public void onScrollEnded() {
        binding.slidingTray.unlock();
    }

    public void setUIOnBrightnessChange(int brightness) {
        binding.content.seekBarBrightness.setProgress(brightness);
    }

    public void updateSeekBar(int volume) {
        binding.content.seekBarVolume.setProgress(volume);
    }

    public void updateSeekBar(int streamType, int currentVolume, int maxVolume) {
        SeekBar seekBar = binding.content.seekBarVolume;
        if (streamType == AudioManager.STREAM_RING) {
            seekBar = binding.content.seekBarVolumeTwo;
        } else if (streamType == AudioManager.STREAM_MUSIC) {
            seekBar = binding.content.seekBarVolumeThree;
        } else if (streamType == AudioManager.STREAM_NOTIFICATION) {
            seekBar = binding.content.seekBarVolumeFour;
        }
        seekBar.setMax(maxVolume);
        seekBar.setProgress(currentVolume);
    }


    public void closeSlidingTray() {
        if (binding.slidingTray.isOpened()) {
            binding.slidingTray.close();
        }
    }

    public void closeSlidingTrayBottom() {
        showAllSoundBar();
    }

    public void setSlidingTrayVisibility(boolean isVisible) {
        binding.slidingTray.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            // TODO check if the comparision works
            if (seekBar == binding.content.seekBarVolume) {
                drawerCallbacks.onVolumeSeek(progress);
            }
            if (seekBar == binding.content.seekBarBrightness) {
                drawerCallbacks.onBrightnessSeek(progress);
            }
        }
    }

    /*public void setScreenBrightness(int brightness, Activity activity){
        // TODO check if the code has any affect
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        layout.screenBrightness = 100F;
        activity.getWindow().setAttributes(layout);

        if (brightness >= 0 && brightness <= 255) {
            Settings.System.putInt(
                    activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightness
            );
        }
    }*/

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setVolumeSeekBar(int currentVolume, int maxVolume) {
        binding.content.seekBarVolume.setMax(maxVolume);
        binding.content.seekBarVolume.setProgress(currentVolume);
    }

//    @Override
//    public void onDrawerClosed() {
//        drawerCallbacks.onDrawerClose();
//    }
//
//    @Override
//    public void onDrawerOpened() {
//        closeSlidingTray();
//        closeSlidingTrayBottom();
//        drawerCallbacks.onDrawerOpen();
//    }

    @Override
    public void onHelperCleanUp() {
        drawerCallbacks = null;
    }

    public void showAllSoundBar() {
        binding.content.volSetting.setImageResource(R.drawable.ic_vol_up);
        binding.content.topDrawerItems.setVisibility(VISIBLE);
        binding.content.emptyView.setVisibility(GONE);
        binding.content.topDrawerSound.setVisibility(GONE);
    }

    public void hideTopDrawerItem() {
        binding.content.volSetting.setImageResource(R.drawable.ic_vol_down);
        binding.content.topDrawerItems.setVisibility(GONE);
        binding.content.emptyView.setVisibility(VISIBLE);
        binding.content.topDrawerSound.setVisibility(VISIBLE);
    }


//    @Override
//    public void onScrollStarted() {
//        LoggerWrapper.i("OnScroll","Started");
////        closeSlidingTray();
////        closeSlidingTrayBottom();
//    }
//
//    @Override
//    public void onScrollEnded() {
//
//    }


    public interface DrawerCallbacks {

        void onVolumeSeek(int volume);

        void onBrightnessSeek(int brightness);

        void onDrawerOpen();

        void onDrawerClose();
    }
}
