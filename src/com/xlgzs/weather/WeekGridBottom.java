package com.xlgzs.weather;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekGridBottom extends LinearLayout {

    private Context mContext;
    private TextView mWind;
    private TextView mWindLevel;
    private TextView mWeather;
    private ImageView mWeatherIcon;

    public WeekGridBottom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public WeekGridBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeekGridBottom(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = inflate(mContext, R.layout.week_grid_bottom, this);
        mWind = (TextView) root.findViewById(R.id.wind);
        mWindLevel = (TextView) root.findViewById(R.id.windLevel);
        mWeatherIcon = (ImageView) root.findViewById(R.id.weatherIcon);
        mWeather = (TextView) root.findViewById(R.id.weather);
    }

    public void setInfo(WeatherInfo.BasicInfo basic) {
        mWeather.setText(basic.weather2);
        int id = mContext.getResources().getIdentifier("ww" + basic.icon2, "drawable", mContext.getPackageName());
        mWeatherIcon.setImageResource((id == 0) ? R.drawable.wna : id);
        mWind.setText(basic.wind.equals("") ? "-" : basic.wind);
        mWindLevel.setText(basic.windL);
    }

}
