package com.sb.woohoo.ui.main.fragments.relaxationmusic;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class RelaxationDiscView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();
    private ValueAnimator animator;
    private float rotation;

    public RelaxationDiscView(Context context) {
        super(context);
        init();
    }

    public RelaxationDiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(9000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            rotation = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
    }

    public void setPlaying(boolean playing) {
        if (playing && !animator.isStarted()) {
            animator.start();
        } else if (!playing && animator.isStarted()) {
            animator.cancel();
            rotation = 0f;
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight());
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = size * 0.42f;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#130A1D"));
        canvas.drawCircle(centerX, centerY, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size * 0.009f);
        paint.setColor(Color.parseColor("#66558B"));
        canvas.drawCircle(centerX, centerY, radius, paint);

        for (int i = 0; i < 18; i++) {
            paint.setStrokeWidth(1f + (i % 4));
            paint.setColor(i % 3 == 0 ? Color.parseColor("#5EE6C8") : Color.parseColor("#9D245F"));
            paint.setAlpha(55 + (i % 5) * 20);
            float inset = size * (0.13f + i * 0.011f);
            arcBounds.set(inset, inset, getWidth() - inset, getHeight() - inset);
            canvas.drawArc(arcBounds, rotation + i * 18f, 60f + (i % 4) * 22f, false, paint);
        }
        paint.setAlpha(255);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#5B344A"));
        canvas.drawCircle(centerX, centerY, size * 0.13f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size * 0.004f);
        paint.setColor(Color.parseColor("#C4869B"));
        canvas.drawCircle(centerX, centerY, size * 0.13f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        float barWidth = size * 0.012f;
        float gap = size * 0.014f;
        float[] heights = new float[]{0.055f, 0.09f, 0.13f, 0.09f, 0.055f};
        float startX = centerX - (barWidth * 2.5f + gap * 2f);
        for (int i = 0; i < heights.length; i++) {
            float h = size * heights[i];
            float x = startX + i * (barWidth + gap);
            canvas.drawRect(x, centerY - h / 2f, x + barWidth, centerY + h / 2f, paint);
        }
    }
}
