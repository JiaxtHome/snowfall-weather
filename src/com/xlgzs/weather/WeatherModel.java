package com.xlgzs.weather;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class WeatherModel {

    private static final String TAG = "weather_data";
    private DataManager mDataMgr;

    interface CallBack {
        void onWeatherInfoUpdateSuccess(WeatherInfo info);
        void onWeatherInfoUpdateFailed();
    }

    private WeakReference<CallBack> mCallback;

    public WeatherModel(Context context) {
        mDataMgr = new DataManager(context);
        mDataMgr.setWeatherModel(this);
    }

    public void setCallBack(CallBack callback) {
        mCallback = new WeakReference<CallBack>(callback);
    }

    public void onWeatherInfoUpdate(WeatherInfo info) {
        if (mCallback == null || mCallback.get() == null) {
            Log.d(TAG, "onWeatherInfoUpdate mCallback == null");
            return;
        }
        mCallback.get().onWeatherInfoUpdateSuccess(info);
    }

    public void requestWeatherInfoUpdate(String city){
        mDataMgr.requestWeatherInfoUpdate(city);
    }

    public List<City> getCities() {
        return mDataMgr.getCities();
    }

    public void addCity(City city) {
        mDataMgr.addCity(city);
    }
}
