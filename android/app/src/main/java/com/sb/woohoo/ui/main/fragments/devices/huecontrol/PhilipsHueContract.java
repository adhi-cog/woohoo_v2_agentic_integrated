package com.sb.woohoo.ui.main.fragments.devices.huecontrol;

import com.sb.woohoo.custom.ColorPicker;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

public interface PhilipsHueContract {
    interface View extends ColorPicker.ColorSelectedListener {

        void showMessage(String message);

        void setUIComponents(Light light);
    }

    interface Actions {

        void updateLight(Light light, int mode);

        int convertToHSV(int i);

        void onAPIError(String message);

        void lightStatusToSocket(Light light);

        void onColorChanged(Light light, int i, boolean isTapUp);
    }
}
