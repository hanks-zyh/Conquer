/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hanks on 2015/6/6.
 */
public class SoundWaveView extends View {

    private int width;
    private int height;

    private int childCount = 10;
    private float dx;

    int colorBackground = Color.parseColor("#c3c3c3");
    int colorProgress   = Color.parseColor("#ED1C24");
    private Paint paint;
    private float progress;

    public SoundWaveView(Context context) {
        this(context, null);
    }

    public SoundWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(colorBackground);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SoundWaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        dx = width * 1.0f / childCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < childCount; i++) {
            paint.setColor(progress >= i * 1.0f / childCount ? colorProgress : colorBackground);
            canvas.drawRect(i * dx, 0, i * dx + dx * 0.67f, height, paint);
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public int getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(int colorBackground) {
        this.colorBackground = colorBackground;
    }

    public int getColorProgress() {
        return colorProgress;
    }

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
    }
}
