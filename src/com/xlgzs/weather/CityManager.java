package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class CityManager {

    private List<City> cities = new ArrayList<City>();
    private Context mContext;
    private int mCurrentCity;
    private int mOrigCity;

    public CityManager(Context context) {
        mContext = context;
        mOrigCity = PreferUtils.getInt(mContext, PreferUtils.KEY_DEFAULT_CITY_INDEX, 0);
        mCurrentCity = mOrigCity;
    }

    public City getCity(int index) {
        return cities.get(index);
    }

    public int getCityCount() {
        return cities.size();
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public void initCity(List<City> cities2) {
        cities.clear();
        cities.addAll(cities2);
    }

    public boolean contains(String cityName) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).name.equals(cityName)) {
                return true;
            }
        }
        return false;
    }

    public City getDefaultCity() {
        int index = PreferUtils.getInt(mContext, PreferUtils.KEY_DEFAULT_CITY_INDEX, 0);
        if (index < cities.size()) {
            return cities.get(index);
        }
        return null;
    }

    public void setCurrentCity(int currIndex) {
        mCurrentCity = currIndex;
    }

    public void saveDefaultCity() {
        if (mOrigCity != mCurrentCity) {
            PreferUtils.setInt(mContext, PreferUtils.KEY_DEFAULT_CITY_INDEX, mCurrentCity);
        }
    }
}
