package com.sb.woohoo.ui.main.fragments.darkMode;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceContract;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceRepository;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.CameraCapturerCompat;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Collections;

import javax.inject.Inject;

public class DarkModePresenter
        extends BasePresenter{

    private DarkModeRepository surveillanceRepository;
    private DarkModeFragment view;

    @Inject
    public DarkModePresenter(DarkModeFragment fragment, DarkModeRepository surveillanceRepository) {
        this.surveillanceRepository = surveillanceRepository;
        this.view = fragment;
        surveillanceRepository.onAttach(this);
    }


    @Override
    public void processActions(IntentInfo intentInfo) {

    }

}
