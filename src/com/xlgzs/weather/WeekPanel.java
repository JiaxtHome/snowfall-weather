package com.xlgzs.weather;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class WeekPanel extends LinearLayout {

    private LinearLayout mGridTop;
    private LinearLayout mGridBottom;
    private DiagramView mDiagram;

    public WeekPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WeekPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekPanel(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mGridTop = (LinearLayout) findViewById(R.id.gridTop);
        mGridBottom = (LinearLayout) findViewById(R.id.gridBottom);
        mDiagram = (DiagramView) findViewById(R.id.diagram);
    }

    public void setInfo(WeatherInfo info) {
        int count = mGridTop.getChildCount();
        Log.i("jxt", "count = "+count);
        for (int i = 0; i < count; i++) {
            ((WeekGridTop) mGridTop.getChildAt(i)).setInfo(info.mWeekInfo.get(i));
            ((WeekGridBottom) mGridBottom.getChildAt(i)).setInfo(info.mWeekInfo.get(i));
        }
        int[] ups = new int[6];
        int[] downs = new int[6];
        for (int i = 0; i < 6; i++) {
            ups[i] = Integer.valueOf(info.mWeekInfo.get(i).temp);
            downs[i] = Integer.valueOf(info.mWeekInfo.get(i).temp2);
        }
        mDiagram.updataData(ups, downs);
    }

}
