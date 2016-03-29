package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.xlgzs.weather.WeatherInfo.BasicInfo;

public class WeatherInfoParser {

    private static final String TAG = "weather_data";

    public WeatherInfoParser() {

    }

    public WeatherInfo parser(String jasonString, String city) {
        if (jasonString == null) {
            Log.e(TAG, "jasonString is null");
            return null;
        }
        WeatherInfo weatherInfo = new WeatherInfo();
        try {
            JSONObject jsonObj = new JSONObject(jasonString);
            JSONObject jsonResult = jsonObj.getJSONObject("result");
            JSONObject jsonData = jsonResult.getJSONObject("data");
            JSONArray jsonArray = jsonData.getJSONArray("weather");
            // get basic info
            if (jsonArray.length() > 0) {
                JSONObject jsonToday = jsonArray.getJSONObject(0);
                String date = jsonToday.getString("date");

                JSONObject jsonInfo = jsonToday.getJSONObject("info");
                JSONArray jsonDay = jsonInfo.getJSONArray("day");
                int icon = jsonDay.getInt(0);
                String weather = jsonDay.getString(1);
                int temp = jsonDay.getInt(2);
                String wind = jsonDay.getString(3);
                String windL = jsonDay.getString(4);

                JSONArray jsonNight = jsonInfo.getJSONArray("night");
                int icon2 = jsonNight.getInt(0);
                String weather2 = jsonNight.getString(1);
                int temp2 = jsonNight.getInt(2);
                String wind2 = jsonNight.getString(3);
                String windL2 = jsonNight.getString(4);

                String week = jsonToday.getString("week");
                String nongli = jsonToday.getString("nongli");

                weatherInfo.setBasicData(city, date, icon, weather, temp, wind, windL, icon2, weather2, temp2, wind2, windL2, week, nongli);
            }
            // get pm2.5
            getPm25(jsonData, weatherInfo);
            // get week info
            List<BasicInfo> weekInfo = new ArrayList<BasicInfo>();
            for (int i = 0; i < jsonArray.length(); i++) {
                BasicInfo basic = new BasicInfo();
                JSONObject oneDay = jsonArray.getJSONObject(i);
                basic.city = city;
                basic.date = oneDay.getString("date");

                JSONObject jsonInfo = oneDay.getJSONObject("info");
                JSONArray jsonDay = jsonInfo.getJSONArray("day");
                basic.icon = jsonDay.getInt(0);
                basic.weather = jsonDay.getString(1);
                basic.temp = jsonDay.getInt(2);
                basic.wind = jsonDay.getString(3);
                basic.windL = jsonDay.getString(4);

                JSONArray jsonNight = jsonInfo.getJSONArray("night");
                basic.icon2 = jsonNight.getInt(0);
                basic.weather2 = jsonNight.getString(1);
                basic.temp2 = jsonNight.getInt(2);
                basic.wind2 = jsonNight.getString(3);
                basic.windL2 = jsonNight.getString(4);

                basic.week = oneDay.getString("week");
                basic.nongli = oneDay.getString("nongli");
                weekInfo.add(basic);
            }
            weatherInfo.setWeekInfo(weekInfo);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "!!!JSONException!!!");
            Log.e(TAG, "jasonString:" + jasonString);
            return null;
        }

        return weatherInfo;
    }

    private void getPm25(JSONObject jsonData, WeatherInfo weatherInfo){
        try {
            JSONObject jsonPm = jsonData.getJSONObject("pm25");
            JSONObject jsonPm25 = jsonPm.getJSONObject("pm25");
            int pm25 = jsonPm25.getInt("pm25");
            String quality = jsonPm25.getString("quality");
            weatherInfo.setPm25(pm25, quality);
        } catch (JSONException e) {
            weatherInfo.setPm25(-1, "N/A");
            e.printStackTrace();
        }
    }
}
