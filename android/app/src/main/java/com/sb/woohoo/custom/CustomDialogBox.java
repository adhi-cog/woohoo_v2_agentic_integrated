package com.sb.woohoo.custom;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;
import static com.sileria.android.Resource.getResources;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.databinding.CustomDialogBoxBinding;
import com.sb.woohoo.utils.others.MethodUtils;

public class CustomDialogBox extends Dialog {

    private Activity c;
    public CustomDialogBoxBinding binding;

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        setActivityFullScreen(c);
        super.setOnCancelListener(listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        setActivityFullScreen(c);
        super.setOnDismissListener(listener);
    }

    @Override
    public void dismiss() {
        setActivityFullScreen(c);
        super.dismiss();
    }

    @Override
    public void cancel() {
        setActivityFullScreen(c);
        super.cancel();
    }

    public CustomDialogBox(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.c = (Activity) context;
        setActivityFullScreen(c);

    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MethodUtils.setDialogMargins(this);
        }
        binding = CustomDialogBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            setDecorations();
        }
        setCancelable(false);
        setActivityFullScreen(c);

    }

    public void setInputType(int editTextInputType) {
        binding.editText.setInputType(editTextInputType);
    }

    private void setDecorations() {
        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.gravity = Gravity.TOP;
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.getDecorView().setSystemUiVisibility(View.GONE);
            window.getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            setActivityFullScreen(window);
        }
    }
}
