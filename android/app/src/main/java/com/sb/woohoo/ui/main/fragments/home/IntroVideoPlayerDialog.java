package com.sb.woohoo.ui.main.fragments.home;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_INTRO_VIDEO_KEY;
import static com.sb.woohoo.utils.Constants.LANGUAGE.ARABIC;
import static com.sb.woohoo.utils.Constants.POSTILLION_DOMAIN_ID;
import static com.sb.woohoo.utils.LocaleHelper.getLanguage;
import static com.sb.woohoo.utils.PlayVideoUtil.buildMediaSource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.DialogIntroViewLayoutBinding;
import com.sb.woohoo.utils.PlayVideoUtil;

import java.util.Objects;

public class IntroVideoPlayerDialog extends Dialog implements Player.Listener {
    private DialogCallback dialogCallback;
    private DialogIntroViewLayoutBinding binding;
    private ExoPlayer player;

    public IntroVideoPlayerDialog(Context context, DialogCallback callback) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.dialogCallback = callback;
    }

    private void showDialog() {
        if (dialogCallback != null) {
            dialogCallback.onDialogOpened();
        }
    }

    private void dismissDialog() {
        if(player!= null) {
            player.stop();
            player.release();
            player = null;
        }

        if (dialogCallback != null) {
            dialogCallback.onDialogClosed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogIntroViewLayoutBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(layoutParams);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        initVideoPlayer();
        showDialog();

        binding.linClose.setOnClickListener(v -> {
            dismissDialog();
            dismiss();
        });
    }

    private void initVideoPlayer()
    {
        String mediaURI = WooHooApp.getSharedPrefsHelper().get(PREF_INTRO_VIDEO_KEY, null);
        if(mediaURI == null){
            Toast.makeText(getContext(), " No video found", Toast.LENGTH_SHORT).show();
            return;
        }
        player = PlayVideoUtil.initialisePlayer(getContext(), player);
        MediaSource lSource = buildMediaSource(getContext(), mediaURI);
        binding.videoView.setPlayer(player);
        player.setPlayWhenReady(true);
        player.addListener(this);
        player.setMediaSource(lSource,false);
        player.prepare();
    }

    private int getVideoBasedOnLanguage() {

        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            return R.raw.intro_video_postillion_english;
        } else {
            if (Objects.equals(getLanguage(getContext()), ARABIC)) {
                return R.raw.intro_video_holiday_inn_arabic;
            }
            return R.raw.intro_video_holiday_inn_english;
        }
    }

    public interface DialogCallback {
        void onDialogOpened();

        void onDialogClosed();
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        /*Player.STATE_IDLE -> {}
          Player.STATE_BUFFERING -> {}
          Player.STATE_READY -> {}
          Player.STATE_ENDED -> {}*/

        if (playbackState == ExoPlayer.STATE_READY){
            player.play();
        }

        if (playbackState == ExoPlayer.STATE_ENDED){
            dismissDialog();
            dismiss();
        }
    }
}
