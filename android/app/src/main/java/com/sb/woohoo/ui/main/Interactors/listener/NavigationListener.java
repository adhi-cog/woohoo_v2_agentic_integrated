package com.sb.woohoo.ui.main.Interactors.listener;

import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

public interface NavigationListener {
    void changeHintBar(int hintText, int backgroundColour, int visibility);

    void resetNlpContext();

    void handleBackPressed();

    boolean isMusicPlaying();

    void pauseMusic();

    void nextMusic();

    void previousMusic();

    void resumeMusic();

    void stopMusic();

    void onResponseToUser(Object response);

    void controlVolume(IntentInfo intentInfo);

    void changeLightState(Light light);

    void stopTimer();
}
