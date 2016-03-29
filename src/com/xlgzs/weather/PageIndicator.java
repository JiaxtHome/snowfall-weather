package com.xlgzs.weather;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndicator extends LinearLayout {

    private int mCurrentPage;
    private Context mContext;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void initPages(int count) {
        initPages(count, 0);
    }

    public void initPages(int count, int focusIndex) {
        removeAllViews();
        if (count < 2) {
            return;
        }
        for (int i = 0; i < count; i++) {
            IndicatorMarker maker = new IndicatorMarker(mContext);
            addView(maker);
            if (focusIndex == i) {
                maker.setFocus(true);
            }
        }
    }

    public void toPage(int index) {

        if (index < 0 || index >= getChildCount()) {
            return;
        }
        ((IndicatorMarker) getChildAt(mCurrentPage)).setFocus(false);
        ((IndicatorMarker) getChildAt(index)).setFocus(true);
        mCurrentPage = index;
    }

    class IndicatorMarker extends ImageView {

        public IndicatorMarker(Context context) {
            super(context);
            setScaleType(ScaleType.CENTER);
            setImageResource(R.drawable.page_indicator);
            int padding = (int) getResources().getDimension(R.dimen.title_marker_margin);
            setPadding(padding, 0, padding, 0);
        }

        public void setFocus(boolean focus) {
            if (focus) {
                setImageResource(R.drawable.page_indicator_focused);
            } else {
                setImageResource(R.drawable.page_indicator);
            }
        }
    }

    public void addOne() {
        if (getChildCount() == 0) {
            IndicatorMarker maker = new IndicatorMarker(mContext);
            addView(maker);
        }
        IndicatorMarker maker = new IndicatorMarker(mContext);
        addView(maker);
        toPage(getChildCount() - 1);
    }
}
