package com.sb.woohoo.ui.main.fragments.livefeedhub;

import com.google.gson.JsonObject;

public interface RotateCamera {
    void rotateLeft(boolean rotate, JsonObject jsonObject);

    void rotateRight(boolean rotate, JsonObject jsonObject);
}