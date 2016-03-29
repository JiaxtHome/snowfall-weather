package com.xlgzs.weather;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ScrimView extends View {

    private Context mContext;
    private boolean mShowing = false;

    public ScrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ScrimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrimView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setBackgroundColor(mContext.getResources().getColor(R.color.black_bg));
        setAlpha(0);
    }

    public void dismiss() {
        if (!mShowing) {
            return;
        }
        mShowing = false;
        animate().alpha(0).setDuration(200).start();
    }

    public void show() {
        if (mShowing) {
            return;
        }
        mShowing = true;
        animate().alpha(1).setDuration(200).start();
    }
}
