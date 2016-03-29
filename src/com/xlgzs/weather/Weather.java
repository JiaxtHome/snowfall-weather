package com.xlgzs.weather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xlgzs.weather.CityPanel.OnCityAddCallback;
import com.xlgzs.weather.GpsHelper.GpsCallback;

public class Weather extends Activity implements WeatherModel.CallBack, PageContainer.PageChangedCallback, OnClickListener, OnCityAddCallback,
        GpsCallback {

    private WeatherModel mWeatherModel;
    private PageContainer mPagerContainer;
    private TitlePanel mTitle;
    private CityManager mCityManager;
    private BackgroundCartoon mCartoon;
    private ScrimView mScrim;
    private Scence mScence;
    private CityPanel mCityPanel;
    private ImageView mMoreCity;
    private GpsHelper mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mWeatherModel = new WeatherModel(this);
        mWeatherModel.setCallBack(this);
        mPagerContainer = (PageContainer) findViewById(R.id.pageContainer);
        mPagerContainer.setPageChangedCallback(this);
        mTitle = (TitlePanel) findViewById(R.id.title);
        mCityManager = new CityManager(this);
        mCartoon = (BackgroundCartoon) findViewById(R.id.bgCartoon);
        mScence = (Scence) findViewById(R.id.scence);
        mScrim = (ScrimView) findViewById(R.id.scrim);
        mPagerContainer.setScrimView(mScrim);
        mCityPanel = (CityPanel) findViewById(R.id.cityMore);
        mCityPanel.setSelectCallback(this);
        mCityPanel.setCityManager(mCityManager);
        mMoreCity = (ImageView) mTitle.findViewById(R.id.moreCity);
        mMoreCity.setOnClickListener(this);
        mGps = new GpsHelper(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetData();
    }

    private void resetData() {
        mCityManager.initCity(mWeatherModel.getCities());
        int count = mCityManager.getCityCount();
        if (count > 0) {
            mPagerContainer.addPages(count);
            mTitle.initPages(count);
            City defCity = mCityManager.getDefaultCity();
            mTitle.setInfo(defCity);
            mPagerContainer.setCurrentPage(defCity.index);
            mWeatherModel.requestWeatherInfoUpdate(defCity.name);
        } else {
            mGps.gpsStart();
        }
    }

    @Override
    public void onWeatherInfoUpdateSuccess(WeatherInfo info) {
        if (info == null) {
            mScence.dismiss(false);
            return;
        }
        for (int i = 0; i < mCityManager.getCityCount(); i++) {
            if (info.mBasicInfo.city.equals(mCityManager.getCity(i).name)) {
                mPagerContainer.getPage(i).setInfo(info);
                if (i == mPagerContainer.getCurrentIndex()) {
                    mCartoon.onWeatherChanged(info);
                }
                break;
            }
        }
        mScence.dismiss(false);
    }

    @Override
    public void onWeatherInfoUpdateFailed() {

    }

    @Override
    public void onPageChanged(int index) {
        mTitle.setInfo(mCityManager.getCity(index));
        mWeatherModel.requestWeatherInfoUpdate(mCityManager.getCity(index).name);
        mCityManager.setCurrentCity(index);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager.getInstance(this).destroy();
        mCityManager.saveDefaultCity();
    }

    @Override
    public void onClick(View v) {
        if (v == mMoreCity) {
            mCityPanel.show();
        }
    }

    @Override
    public void onCityAdd(City city) {
        Log.i("wdb", "onSelect" + city.name);
        mPagerContainer.addPage();
        mTitle.addCity(city.name);
        mWeatherModel.addCity(city);
    }

    @Override
    public void onLocationResult(String cityName) {
        if (cityName == null) {
            Log.i("gps", "gps failed ");
            return;
        }
        if (mCityManager.contains(cityName)) {
            Log.i("gps", "gps result duplicate ");
            return;
        }
        Log.i("gps", "gps location city is " + cityName);
        City city = new City();
        city.index = 0;
        city.name = cityName;
        mWeatherModel.addCity(city);
        resetData();
    }

}
