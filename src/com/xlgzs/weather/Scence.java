package com.xlgzs.weather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class Scence extends FrameLayout {

    private static final long STAY = 2000;
    long mStayTime;

    public Scence(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Scence(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mStayTime = System.currentTimeMillis();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void dismiss(boolean immediately) {
        if (immediately) {
            setVisibility(View.GONE);
            return;
        }
        long time = System.currentTimeMillis();
        long delay = 0;
        if (time - mStayTime < STAY) {
            delay = STAY - (time - mStayTime);
        }
        ValueAnimator va = ValueAnimator.ofFloat(1f, 0);
        va.setDuration(300);
        va.setStartDelay(delay);
        va.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float value = (Float) arg0.getAnimatedValue();
                setAlpha(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(View.GONE);
            }
        });
        va.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

}
