package com.sb.woohoo.ui.main.callbacks;

import android.widget.TextView;

public interface AnimationListener {

    void onAnimationComplete();

    void onUpdateText(TextView animatedTextView, String text);
}
