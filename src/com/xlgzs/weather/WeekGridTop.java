package com.xlgzs.weather;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekGridTop extends LinearLayout {

    private Context mContext;
    private TextView mWeek;
    private TextView mDate;
    private ImageView mWeatherIcon;
    private TextView mWeather;
    private SimpleDateFormat mSdfWeek;

    public WeekGridTop(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public WeekGridTop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeekGridTop(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSdfWeek = new SimpleDateFormat("EE", Locale.CHINA);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = inflate(mContext, R.layout.week_grid_top, this);
        mWeek = (TextView) root.findViewById(R.id.week);
        mDate = (TextView) root.findViewById(R.id.date);
        mWeather = (TextView) root.findViewById(R.id.weather);
        mWeatherIcon = (ImageView) root.findViewById(R.id.weatherIcon);
    }

    public void setInfo(WeatherInfo.BasicInfo basic) {
        String[] dataSplit = basic.date.split("-");
        String date = dataSplit[1] + "/" + dataSplit[2];
        String week = "周" + basic.week;
        String weekToday = mSdfWeek.format(new Date(System.currentTimeMillis()));
        mWeek.setText(week.equals(weekToday) ? "今天" : week);
        mDate.setText(date);
        mWeather.setText(basic.weather);
        int id = mContext.getResources().getIdentifier("ww" + basic.icon, "drawable", mContext.getPackageName());
        mWeatherIcon.setImageResource((id == 0) ? R.drawable.wna : id);
    }

}
