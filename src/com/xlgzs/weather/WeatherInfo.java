package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherInfo {

    public static final int DAY_COUNT = 6;
    static class BasicInfo {
        String city;
        String date;
        int icon;
        String weather;
        int temp;
        String wind;
        String windL;
        int icon2;
        String weather2;
        int temp2;
        String wind2;
        String windL2;
        String week;
        String nongli;
    }

    int pm25;
    String pmQuality;
    BasicInfo mBasicInfo;
    List<BasicInfo> mWeekInfo;

    public WeatherInfo() {
        mBasicInfo = new BasicInfo();
        mWeekInfo = new ArrayList<BasicInfo>();
    }

    public void setBasicData(String city, String date, int icon, String weather, int temp, String wind, String windL, int icon2, String weather2,
            int temp2, String wind2, String windL2, String week, String nongli) {
        mBasicInfo.city = city;
        mBasicInfo.date = date;
        mBasicInfo.icon = icon;
        mBasicInfo.weather = weather;
        mBasicInfo.temp = temp;
        mBasicInfo.wind = wind;
        mBasicInfo.windL = windL;
        mBasicInfo.icon2 = icon2;
        mBasicInfo.weather2 = weather2;
        mBasicInfo.temp2 = temp2;
        mBasicInfo.wind2 = wind2;
        mBasicInfo.windL2 = windL2;
        mBasicInfo.week = week;
        mBasicInfo.nongli = nongli;
    }

    public void setBasicData(WeatherInfo.BasicInfo basic){
        mBasicInfo.city = basic.city;
        mBasicInfo.date = basic.date;
        mBasicInfo.icon = basic.icon;
        mBasicInfo.weather = basic.weather;
        mBasicInfo.temp = basic.temp;
        mBasicInfo.wind = basic.wind;
        mBasicInfo.windL = basic.windL;
        mBasicInfo.icon2 = basic.icon2;
        mBasicInfo.weather2 = basic.weather2;
        mBasicInfo.temp2 = basic.temp2;
        mBasicInfo.wind2 = basic.wind2;
        mBasicInfo.windL2 = basic.windL2;
        mBasicInfo.week = basic.week;
        mBasicInfo.nongli = basic.nongli;
    }

    public void setPm25(int pm25, String level) {
        this.pm25 = pm25;
        this.pmQuality = level;
    }

    public void setWeekInfo(List<BasicInfo> weekInfo) {
        mWeekInfo.clear();
        mWeekInfo.addAll(weekInfo);
    }

}
