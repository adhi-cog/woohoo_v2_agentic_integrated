package com.sb.woohoo.ui.main.fragments.livefeedhubaudio;

import com.google.gson.JsonObject;

public interface RotateCameraAudio {
    void rotateLeft(boolean rotate, JsonObject jsonObject);

    void rotateRight(boolean rotate, JsonObject jsonObject);
}