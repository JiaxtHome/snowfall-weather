package com.xlgzs.weather;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xlgzs.weather.VoiceManager.PlayCallback;

public class TodayPanel extends FrameLayout implements PlayCallback {

    private ImageView mPm25Icon;
    private TextView mPm25;
    private TextView mWeatherContent;
    private TextView mTemperature;
    private TextView mWindLevel;
    private TextView mWind;
    private WeatherInfo mWeather;
    private ImageView mVoice;
    private Context mContext;
    private AnimationDrawable mBroadcast;
    private LinearLayout mPm25Container;

    public TodayPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public TodayPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TodayPanel(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPm25 = (TextView) findViewById(R.id.pm25);
        mPm25Icon = (ImageView) findViewById(R.id.pm25Icon);
        mPm25Container = (LinearLayout) findViewById(R.id.mp25Container);
        mWeatherContent = (TextView) findViewById(R.id.weather);
        mTemperature = (TextView) findViewById(R.id.temperature);
        mWindLevel = (TextView) findViewById(R.id.windLevel);
        mWind = (TextView) findViewById(R.id.wind);
        mVoice = (ImageView) findViewById(R.id.voice);
        mBroadcast = (AnimationDrawable) mVoice.getBackground();
        mVoice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mWeather != null) {
                    VoiceManager.getInstance(mContext).play(mWeather, TodayPanel.this);
                }
            }
        });
    }

    public void setInfo(WeatherInfo info) {
        mWeather = info;
        if (info.pm25 > 0){
            mPm25Container.setVisibility(View.VISIBLE);
            mPm25.setText(info.pm25 + " " + info.pmQuality);
        } else {
            mPm25Container.setVisibility(View.GONE);
        }
        mWeatherContent.setText(Utils.isNight() ? info.mBasicInfo.weather2 : info.mBasicInfo.weather);
        String temp = info.mBasicInfo.temp2 + "~" + info.mBasicInfo.temp + "°C";
        mTemperature.setText(temp);
        mWindLevel.setText(Utils.isNight() ? info.mBasicInfo.windL2 : info.mBasicInfo.windL);
        mWind.setText(Utils.isNight() ? info.mBasicInfo.wind2 : info.mBasicInfo.wind);
    }

    @Override
    public void onPlayStart() {
        if (mBroadcast != null) {
            mBroadcast.start();
        }
    }

    @Override
    public void onPlayStop() {
        if (mBroadcast != null) {
            mBroadcast.selectDrawable(0);
            mBroadcast.stop();
        }
    }

}
