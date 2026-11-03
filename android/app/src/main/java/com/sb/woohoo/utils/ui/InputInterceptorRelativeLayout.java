package com.sb.woohoo.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.sb.woohoo.ui.main.MainActivity;

public class InputInterceptorRelativeLayout extends RelativeLayout {

    MainActivity mainActivity;

    public InputInterceptorRelativeLayout(Context context) {
        super(context);
    }

    public InputInterceptorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputInterceptorRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputInterceptorRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setContext(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (mainActivity != null) {
            mainActivity.onPushAutoClose();
        }
        return super.dispatchTouchEvent(e);
    }
}
