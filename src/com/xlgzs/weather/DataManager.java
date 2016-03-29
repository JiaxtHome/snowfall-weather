package com.xlgzs.weather;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.xlgzs.weather.Client.OnUpdateListener;

public class DataManager implements OnUpdateListener{

    private static final String TAG = "weather_data";
    private WeatherInfo mWeatherInfo;
    private Client mClient;
    private WeatherModel mWeatherModel;
    private Context mContext;
    private DbHelper mDb;

    public DataManager(Context context) {
        mWeatherInfo = new WeatherInfo();
        mClient = new Client(context);
        mClient.setUpdateListener(this);
        mDb = new DbHelper(context);
    }

    public void setWeatherModel(WeatherModel model){
        mWeatherModel = model;
    }

    public WeatherInfo getTodayInfo() {
        return mWeatherInfo;
    }

    public void onDataFreshed(WeatherInfo info) {
        mWeatherInfo = info;
    }

    public void requestWeatherInfoUpdate(String city) {
        WeatherInfo info = mDb.fetchWeather(city, Utils.getTimesLastHour());
        if (info == null){
            Log.i("wdb", "db has no data of " + city + "will get from server");
            mClient.requestWeatherInfoUpdate(city);
        } else {
            Log.i("wdb", "db already has data of " + city);
            if (mWeatherModel == null){
                Log.d(TAG, "onDataUpdate mWeatherModel == null");
                return;
            }
            mWeatherModel.onWeatherInfoUpdate(info);
        }
    }

    @Override
    public void onWeatherInfoUpdateSuccess(WeatherInfo info) {
        if (mWeatherModel == null){
            Log.d(TAG, "onDataUpdate mWeatherModel == null");
            return;
        }
        if (info == null){
            Log.d(TAG, "onDataUpdate WeatherInfo == null");
            return;
        }
        mWeatherModel.onWeatherInfoUpdate(info);
        mDb.insertWeather(info, Utils.getTimesLastHour());
    }

    @Override
    public void onWeatherInfoUpdateFailed() {
        
    }

    public List<City> getCities() {
        return mDb.fetchCity();
    }

    public void addCity(City city) {
        mDb.insertCity(city);
    }
}
