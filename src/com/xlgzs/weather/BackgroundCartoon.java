package com.xlgzs.weather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class BackgroundCartoon extends FrameLayout {

    private Context mContext;
    private CartoonSurface mCartoonSurface;

    public BackgroundCartoon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundCartoon(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCartoonSurface = (CartoonSurface) findViewById(R.id.surface);
    }

    public void setInfo(WeatherInfo info) {

    }

    public void onWeatherChanged(WeatherInfo info) {
        mCartoonSurface.stop();
        Bitmap b = mCartoonSurface.getCacheBitmap();
        ImageView img = new ImageView(mContext);
        img.setScaleType(ScaleType.FIT_XY);
        img.setImageBitmap(b);
        addView(img);
        mCartoonSurface.onWeatherChanged(Utils.isNight() ? info.mBasicInfo.icon2 : info.mBasicInfo.icon);
        aniamteDismiss(img);
    }

    private void aniamteDismiss(final View v) {
        ValueAnimator va = ValueAnimator.ofFloat(1f, 0);
        va.setDuration(1000);
        va.setStartDelay(300);
        va.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float value = (Float) arg0.getAnimatedValue();
                v.setAlpha(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setAlpha(1);
                mCartoonSurface.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setAlpha(0);
                if (getChildCount() > 1) {
                    removeView(v);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                v.setAlpha(0);
                if (getChildCount() > 1) {
                    removeView(v);
                }
            }

        });
        va.start();
    }

}
