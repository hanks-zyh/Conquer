/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.view;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;


/**
 * Created by Hanks on 2015/5/27.
 */
public class OpAnimationView extends View {

    private Paint paint;
    private Paint paintShadow;

    private int width;
    private int height;
    private int c;

    private boolean isAnimating  = false;    //is isAnimating?
    private boolean isRgihtShape = false;    //is in right_shap?

    private float progress;        //animation‘s progress
    private int DURATION = 300;      // animation DURATION
    private int r;        //randius
    private int shadowWidth;        //shadow randius
    private int defaultShapeWidth;
    private int shapeWidth;
    private int shapeRadius;
    private int defaultShapeRadius;

    public OpAnimationView(Context context) {
        this(context, null);
    }

    public OpAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OpAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OpAnimationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * init paint
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        defaultShapeWidth = dp2px(4);
        defaultShapeRadius = dp2px(7);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        setLayerType(LAYER_TYPE_SOFTWARE, paintShadow);

        shadowWidth = dp2px(2);
        paintShadow.setShadowLayer(shadowWidth, 0.0f, dp2px(1), Color.parseColor("#66000000"));

        // load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.OpAnimationView, defStyleAttr, 0);

        setBackgroundColor(attributes.getColor(R.styleable.OpAnimationView_backgroundColor, Color.parseColor("#FF4081")));
        setShapeColor(attributes.getColor(R.styleable.OpAnimationView_shapeColor, Color.parseColor("#ffffff")));
        shapeWidth = attributes.getDimensionPixelOffset(R.styleable.OpAnimationView_shapeWidth, defaultShapeWidth);
        shapeRadius = attributes.getDimensionPixelOffset(R.styleable.OpAnimationView_shapeRadius, defaultShapeRadius);
        setShapeWidth(shapeWidth);
        setShapeRadius(shapeRadius);

        // We no longer need our attributes TypedArray, give it back to cache
        attributes.recycle();

    }


    private List<PointF> points = new ArrayList<>();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        r = Math.min(w, h) / 2 - shadowWidth;
        float g2 = (float) Math.sqrt(2);
        c = (int) (r / g2);
        float dis = r - c + dp2px(4);

        float x = w / 2;
        float y = h / 2;

        points.clear();


        float d = shapeWidth / g2 / 2;
        points.add(new PointF(x, y - shapeRadius));
        points.add(new PointF(x, y + shapeRadius));
        points.add(new PointF(x - shapeRadius, y));
        points.add(new PointF(x + shapeRadius, y));
        points.add(new PointF(x - shapeRadius - shapeRadius, y));
        points.add(new PointF(x - (2 - g2) * shapeRadius + d, y + g2 * shapeRadius + d));
        points.add(new PointF(x - (2 - g2) * shapeRadius - d, y + g2 * shapeRadius + d));
        points.add(new PointF(x + (3 * g2 - 2) * shapeRadius, y - g2 * shapeRadius));


        /*points.add(new PointF(c + dis, c / 3f + dis));
        points.add(new PointF(c + dis, c * 5f / 3f + dis));
        points.add(new PointF(c / 3f + dis, c + dis));
        points.add(new PointF(c * 5f / 3f + dis, c + dis));
        points.add(new PointF(c * (1 + 3 * g2) / 3f + dis, c * (3 - g2) / 3f + dis));
        points.add(new PointF(c * (1 + g2) / 3f + shapeWidth / g2 / 2 + dis, c * (3 + g2) / 3f + shapeWidth / g2 / 2 + dis));
        points.add(new PointF(c * (1 + g2) / 3f - shapeWidth / g2 / 2 + dis, c * (3 + g2) / 3f + shapeWidth / g2 / 2 + dis));*/
    }

    //set background circle color
    public void setBackgroundColor(int color) {
        paintShadow.setColor(color);
    }

    //set center shape color
    public void setShapeColor(int color) {
        paint.setColor(color);
    }

    public void setShapeWidth(int width) {
        paint.setStrokeWidth(width);
    }

    //set the add_shape's radius
    public void setShapeRadius(int radius) {
        shapeRadius = radius;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(width / 2, height / 2, r, paintShadow);
/*
        if (isRgihtShape) { // right --> add
            float x0 = (points.get(0).x - points.get(4).x) * progress + points.get(4).x;
            float y0 = (points.get(0).y - points.get(4).y) * progress + points.get(4).y;
            float x1 = (points.get(1).x - points.get(6).x) * progress + points.get(6).x;
            float y1 = (points.get(1).y - points.get(6).y) * progress + points.get(6).y;

            float x2 = points.get(2).x;
            float y2 = points.get(2).y;
            float x3 = (points.get(3).x - points.get(5).x) * progress + points.get(5).x;
            float y3 = (points.get(3).y - points.get(5).y) * progress + points.get(5).y;
            canvas.drawLine(x0, y0, x1, y1, paint);
            canvas.drawLine(x2, y2, x3, y3, paint);

        } else { //add --> right

            float x0 = (points.get(4).x - points.get(0).x) * progress + points.get(0).x;
            float y0 = (points.get(4).y - points.get(0).y) * progress + points.get(0).y;
            float x1 = (points.get(6).x - points.get(1).x) * progress + points.get(1).x;
            float y1 = (points.get(6).y - points.get(1).y) * progress + points.get(1).y;

            float x2 = points.get(2).x;
            float y2 = points.get(2).y;
            float x3 = (points.get(5).x - points.get(3).x) * progress + points.get(3).x;
            float y3 = (points.get(5).y - points.get(3).y) * progress + points.get(3).y;
            canvas.drawLine(x0, y0, x1, y1, paint);
            canvas.drawLine(x2, y2, x3, y3, paint);
        }*/

        if (isRgihtShape) { // right --> add
            float x0 = (points.get(0).x - points.get(7).x) * progress + points.get(7).x;
            float y0 = (points.get(0).y - points.get(7).y) * progress + points.get(7).y;
            float x1 = (points.get(1).x - points.get(6).x) * progress + points.get(6).x;
            float y1 = (points.get(1).y - points.get(6).y) * progress + points.get(6).y;

            float x2 = (points.get(2).x - points.get(4).x) * progress + points.get(4).x;
            float y2 = (points.get(2).y - points.get(4).y) * progress + points.get(4).y;

            float x3 = (points.get(3).x - points.get(5).x) * progress + points.get(5).x;
            float y3 = (points.get(3).y - points.get(5).y) * progress + points.get(5).y;
            canvas.drawLine(x0, y0, x1, y1, paint);
            canvas.drawLine(x2, y2, x3, y3, paint);

        } else { //add --> right

            float x0 = (points.get(7).x - points.get(0).x) * progress + points.get(0).x;
            float y0 = (points.get(7).y - points.get(0).y) * progress + points.get(0).y;
            float x1 = (points.get(6).x - points.get(1).x) * progress + points.get(1).x;
            float y1 = (points.get(6).y - points.get(1).y) * progress + points.get(1).y;

            float x2 = (points.get(4).x - points.get(2).x) * progress + points.get(2).x;
            float y2 = (points.get(4).y - points.get(2).y) * progress + points.get(2).y;

            float x3 = (points.get(5).x - points.get(3).x) * progress + points.get(3).x;
            float y3 = (points.get(5).y - points.get(3).y) * progress + points.get(3).y;
            canvas.drawLine(x0, y0, x1, y1, paint);
            canvas.drawLine(x2, y2, x3, y3, paint);
        }
    }

    public boolean isRightShape() {
        return isRgihtShape;
    }

    /**
     * add_shape to right_shape
     */
    public void add2right() {
        if (isAnimating) {
            return;
        }
        progress = 0f;
        isAnimating = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                if (progress >= 1) {
                    isRgihtShape = true;
                    isAnimating = false;
                } else {
                    invalidate();
                }
            }
        });
        valueAnimator.start();
    }


    /**
     * right_shape  to add_shape
     */
    public void right2add() {
        if (isAnimating) {
            return;
        }
        progress = 0f;
        isAnimating = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                if (progress >= 1) {
                    isRgihtShape = false;
                    isAnimating = false;
                } else {
                    invalidate();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * dp×ª px.
     *
     * @param value the value
     * @return the int
     */
    public int dp2px(float value) {
        final float scale = getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }
}
