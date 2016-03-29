package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper {

    private static final String DB_NAME = "snowfell.db"; // 数据库名
    private static final int DB_VERSION = 1; // 数据库版本

    private static final String DB_TABLE_WEATHER = "weather"; // weather表名
    private static final String KEY_ID = "_id"; // id
    private static final String KEY_CITY = "city";
    private static final String KEY_FUTURE = "future";
    private static final String KEY_DATE = "date";
    private static final String KEY_ICON = "icon";
    private static final String KEY_WEATHER = "weather";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_WIND = "wind";
    private static final String KEY_WINDL = "windl";
    private static final String KEY_ICON2 = "icon2";
    private static final String KEY_WEATHER2 = "weather2";
    private static final String KEY_TEMP2 = "temp2";
    private static final String KEY_WIND2 = "wind2";
    private static final String KEY_WINDL2 = "windl2";
    private static final String KEY_WEEK = "week";
    private static final String KEY_NONGLI = "nongli";
    private static final String KEY_PM25 = "pm25";
    private static final String KEY_PM25L = "pm25l";

    private static final String DB_TABLE_CITY = "city"; // city表名
    private static final String KEY_NAME = "name";
    private static final String KEY_INDEX = "cityOrder";
    private static final String KEY_CODE = "code";

    private Context context;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        // 创建数据库语句
        private static final String DB_CREAT_WEATHER = 
                "CREATE TABLE " + DB_TABLE_WEATHER + " (" 
                + KEY_ID + " INTEGER," 
                + KEY_CITY + " TEXT,"
                + KEY_FUTURE + " INTEGER,"
                + KEY_DATE + " TEXT,"
                + KEY_ICON + " INTEGER,"
                + KEY_WEATHER + " TEXT,"
                + KEY_TEMP + " INTEGER,"
                + KEY_WIND + " TEXT,"
                + KEY_WINDL + " TEXT,"
                + KEY_ICON2 + " INTEGER,"
                + KEY_WEATHER2 + " TEXT,"
                + KEY_TEMP2 + " INTEGER,"
                + KEY_WIND2 + " TEXT,"
                + KEY_WINDL2 + " TEXT,"
                + KEY_WEEK + " TEXT,"
                + KEY_NONGLI + " TEXT,"
                + KEY_PM25 + " INTEGER,"
                + KEY_PM25L + " INTEGER)";

        private static final String DB_CREAT_CITY = 
                "CREATE TABLE " + DB_TABLE_CITY + " (" 
                + KEY_INDEX + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_CODE + " INTEGER)";

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREAT_WEATHER);
            db.execSQL(DB_CREAT_CITY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_WEATHER);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CITY);
            onCreate(db);
        }
    }

    public DbHelper(Context context) {
        this.context = context;
        addDefaultCity(context);
    }

    private void addDefaultCity(Context context) {
//        boolean firstLaunch = context.getSharedPreferences("db_sp", Context.MODE_PRIVATE).getBoolean("isFirstLaunch", true);
//        if (firstLaunch) {
//            context.getSharedPreferences("db_sp", Context.MODE_PRIVATE).edit().putBoolean("isFirstLaunch", false).apply();
//            String[] def = new String[] { "温江", "成都", "北京", "拉萨", "呼和浩特" };
//            for (int i = 0; i < def.length; i++) {
//                insertCity(def[i], i);
//            }
//        }
    }

    // 开启
    private SQLiteDatabase getDatabase() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context);
        }
        if (mSQLiteDatabase == null) {
            mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mSQLiteDatabase;
    }

    // 关闭
    public void close() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
        if (mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
    }

    // 增
    public long insertWeather(WeatherInfo info, long _id) {
        deleteWeather(info.mBasicInfo.city);
        long id = 0;
        for (int i = 0; i < info.mWeekInfo.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, _id);
            String city = info.mWeekInfo.get(i).city;
            values.put(KEY_CITY, city);
            values.put(KEY_FUTURE, i);
            values.put(KEY_DATE, info.mWeekInfo.get(i).date);
            values.put(KEY_ICON, info.mWeekInfo.get(i).icon);
            values.put(KEY_WEATHER, info.mWeekInfo.get(i).weather);
            values.put(KEY_TEMP, info.mWeekInfo.get(i).temp);
            values.put(KEY_WIND, info.mWeekInfo.get(i).wind);
            values.put(KEY_WINDL, info.mWeekInfo.get(i).windL);
            values.put(KEY_ICON2, info.mWeekInfo.get(i).icon2);
            values.put(KEY_WEATHER2, info.mWeekInfo.get(i).weather2);
            values.put(KEY_TEMP2, info.mWeekInfo.get(i).temp2);
            values.put(KEY_WIND2, info.mWeekInfo.get(i).wind2);
            values.put(KEY_WINDL2, info.mWeekInfo.get(i).windL2);
            values.put(KEY_WEEK, info.mWeekInfo.get(i).week);
            values.put(KEY_NONGLI, info.mWeekInfo.get(i).nongli);
            values.put(KEY_PM25, (i == 0) ? info.pm25 : -1);
            values.put(KEY_PM25L, (i == 0) ? info.pmQuality : "N/A");
            id = getDatabase().insert(DB_TABLE_WEATHER, null, values);
        }

        Log.i("wdb", "last insert id = " + id);
        return id;
    }

    // 删
    public boolean deleteWeather(String cityname, long notId) {
        boolean delete = getDatabase().delete(DB_TABLE_WEATHER, KEY_CITY + "=" + cityname + " and " + KEY_ID + "<>" + notId, null) > 0;
        return delete;
    }

    public boolean deleteWeather(String cityname) {
        boolean delete = getDatabase().delete(DB_TABLE_WEATHER, KEY_CITY + "=?", new String[]{cityname}) > 0;
        return delete;
    }

    // 查
    public WeatherInfo fetchWeather(String city, long id) {
        Cursor mCursor = getDatabase().query(DB_TABLE_WEATHER, null, KEY_CITY + "=? and " + KEY_ID + "=?", new String[]{city, String.valueOf(id)}, null, null, KEY_FUTURE + " asc");
        WeatherInfo info = null;
        Log.i("wdb", "fetching... ");
        if (mCursor != null) {
            Log.i("wdb", "mCursor count = " + mCursor.getCount());
            if (mCursor.getCount() >= WeatherInfo.DAY_COUNT) {
                Log.i("wdb", "fetching hit!!!");
                info = new WeatherInfo();
                List<WeatherInfo.BasicInfo> basics = new ArrayList<WeatherInfo.BasicInfo>();
                while (mCursor.moveToNext()) {
                    WeatherInfo.BasicInfo basic = new WeatherInfo.BasicInfo();
                    basic.city = mCursor.getString(mCursor.getColumnIndex(KEY_CITY));
                    basic.date = mCursor.getString(mCursor.getColumnIndex(KEY_DATE));
                    basic.icon = mCursor.getInt(mCursor.getColumnIndex(KEY_ICON));
                    basic.icon2 = mCursor.getInt(mCursor.getColumnIndex(KEY_ICON2));
                    basic.nongli = mCursor.getString(mCursor.getColumnIndex(KEY_NONGLI));
                    basic.temp = mCursor.getInt(mCursor.getColumnIndex(KEY_TEMP));
                    basic.temp2 = mCursor.getInt(mCursor.getColumnIndex(KEY_TEMP2));
                    basic.weather = mCursor.getString(mCursor.getColumnIndex(KEY_WEATHER));
                    basic.weather2 = mCursor.getString(mCursor.getColumnIndex(KEY_WEATHER2));
                    basic.week = mCursor.getString(mCursor.getColumnIndex(KEY_WEEK));
                    basic.wind = mCursor.getString(mCursor.getColumnIndex(KEY_WIND));
                    basic.wind2 = mCursor.getString(mCursor.getColumnIndex(KEY_WIND2));
                    basic.windL = mCursor.getString(mCursor.getColumnIndex(KEY_WINDL));
                    basic.windL2 = mCursor.getString(mCursor.getColumnIndex(KEY_WINDL2));
                    basic.windL2 = mCursor.getString(mCursor.getColumnIndex(KEY_WINDL2));
                    if (mCursor.isFirst()) {
                        info.setBasicData(basic);
                        int pm25 = mCursor.getInt(mCursor.getColumnIndex(KEY_PM25));
                        String quality = mCursor.getString(mCursor.getColumnIndex(KEY_PM25L));
                        info.setPm25(pm25, quality);
                    }
                    basics.add(basic);
                }
                info.setWeekInfo(basics);
            } else if (mCursor.getCount() > 0){
                deleteWeather(city);
            }
        }
        return info;
    }

    public void insertCities(List<String> cities) {
        for (int i = 0; i < cities.size(); i++) {
            insertCity(cities.get(i), i);
        }
    }

    public long insertCity(City city){
        return insertCity(city.name, city.index);
    }

    public long insertCity(String name, long index) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_INDEX, index);
        long id = getDatabase().insert(DB_TABLE_CITY, null, values);

        return id;
    }

    public boolean deleteCity(String cityName) {
        boolean delete = getDatabase().delete(DB_TABLE_CITY, KEY_NAME + "=?", new String[] { cityName }) > 0;
        return delete;
    }

    public List<City> fetchCity() {
        Cursor mCursor = getDatabase().query(DB_TABLE_CITY, null, null, null, null, null, KEY_INDEX + " asc");
        List<City> cities = new ArrayList<City>();
        Log.i("wdb", "fetching city... ");
        while (mCursor.moveToNext()) {
            City city = new City();
            city.name = mCursor.getString(mCursor.getColumnIndex(KEY_NAME));
            city.index = mCursor.getInt(mCursor.getColumnIndex(KEY_INDEX));
            city.code = mCursor.getInt(mCursor.getColumnIndex(KEY_CODE));
            cities.add(city);
        }
        return cities;
    }
}
