package com.sb.woohoo.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.R;
import com.sb.woohoo.utils.others.DrawableUtilities;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

public class ColorPicker extends RelativeLayout {

    private ImageView ivColorPicker;
    private ImageView viewBg;
    private Bitmap drawableToBitmap;
    private ColorSelectedListener colorSelectedListener;

    public ColorPicker(Context context) {
        this(context, null, -1);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setColorSelectedListener(ColorSelectedListener colorSelectedListener) {
        this.colorSelectedListener = colorSelectedListener;
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.content_color_picker, this, true);
        ivColorPicker = view.findViewById(R.id.iv_color_picker);
        viewBg = view.findViewById(R.id.view_bg);
    }

    public void setGradientView(int resource) {
        if (resource == -1) {
            throw new RuntimeException("resource cannot be null");
        }
        drawableToBitmap = DrawableUtilities.
                drawableToBitmap(ContextCompat.getDrawable(getContext(), resource));
        viewBg.setImageBitmap(drawableToBitmap);
        setScreenTouchListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setScreenTouchListener() {
        viewBg.setOnTouchListener((v, event) -> {
            ColorPicker.this.getParent().requestDisallowInterceptTouchEvent(true);
            int y = (int) (event.getY() - ivColorPicker.getHeight() / 2);
            int x = (int) (event.getX() - ivColorPicker.getWidth() / 2);

            if (x < -ivColorPicker.getWidth() / 2) {
                x = -ivColorPicker.getWidth() / 2;
            }
            if (y < -ivColorPicker.getHeight() / 2) {
                y = -ivColorPicker.getHeight() / 2;
            }
            if (x > viewBg.getWidth() - ivColorPicker.getWidth() / 2) {
                x = viewBg.getWidth() - ivColorPicker.getWidth() / 2;
            }
            if (y > viewBg.getHeight() - ivColorPicker.getHeight() / 2) {
                y = viewBg.getHeight() - ivColorPicker.getHeight() / 2;
            }
            ivColorPicker.setX(x);
            ivColorPicker.setY(y);

            float newy = ((event.getY()) / viewBg.getHeight()) * drawableToBitmap.getHeight();
            float newx = ((event.getX() / viewBg.getWidth()) * drawableToBitmap.getWidth());

            if (newx < 0) {
                newx = 0;
            }
            if (newy < 0) {
                newy = 0;
            }
            if (newx >= drawableToBitmap.getWidth()) {
                newx = drawableToBitmap.getWidth() - 1;
            }
            if (newy >= drawableToBitmap.getHeight()) {
                newy = drawableToBitmap.getHeight() - 1;
            }
            int color = drawableToBitmap.getPixel((int) newx,
                    (int) newy);

            LoggerWrapper.i(ColorPicker.class.getSimpleName(), "RGB : [ " + Color.red(color) + "\t" + Color.green(color) + "\t" + Color.blue(color) + " ]");

            ((GradientDrawable) ivColorPicker.getBackground()).setColor(color);

            if (colorSelectedListener != null) {
                colorSelectedListener.onColorSelected(color, false);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                ColorPicker.this.getParent().requestDisallowInterceptTouchEvent(false);
                if (colorSelectedListener != null) {
                    colorSelectedListener.onColorSelected(color, true);
                }
            }
            return true;
        });
    }

    public interface ColorSelectedListener {

        void onColorSelected(@ColorInt int color, boolean isTapUp);
    }
}
