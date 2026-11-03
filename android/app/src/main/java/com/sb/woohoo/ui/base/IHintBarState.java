package com.sb.woohoo.ui.base;

public interface IHintBarState {

    int HINT_NO_CHANGE = -1;
    int HINT_RESET = -2;

    void setHint(int hintText, int backgroundColour, int visibility);

    interface Text {
        int HINT_EMPTY = -3;
    }


    interface Background {
        int HINT_TRANSPARENT = -4;
    }
}
