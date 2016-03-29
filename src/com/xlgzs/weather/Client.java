package com.xlgzs.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

public class Client {

    private static final String TAG = "wdb";
    private static final String WEATHER_URL_360 = "http://api.lib360.net/open/weather.json?city=";
    private static final String WEATHER_URL_JUHE_SDK = "http://op.juhe.cn/onebox/weather/query";
    private static final int WEATHER_JUHE_API_ID = 73;
    private static final String WEATHER_URL_JUHE = "http://op.juhe.cn/onebox/weather/query?key=e9d4d12bf4304c2ce804a1ae003dc68e&cityname=";
    private static final int LOAD_SUCCESS = 1;
    private static final int LOAD_FAILED = 2;
    private DefaultHttpClient mHttpClient;
    private WeatherInfoParser mParser;
    private String mResult;
    private boolean mLoading = false;
    private List<String> mPendingList = new ArrayList<String>();
    private String mCurrCity;
    private Context mContext;

    interface OnUpdateListener {
        void onWeatherInfoUpdateSuccess(WeatherInfo info);

        void onWeatherInfoUpdateFailed();
    }

    class WeatherGetThread extends Thread {
        String sCity;

        public WeatherGetThread(String city) {
            sCity = city;
        }

        @Override
        public void run() {
            doPostJuhe(sCity);
            // doPost360(sCity);
        }

    }

    private OnUpdateListener mUpdateListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case LOAD_SUCCESS:
                if (mUpdateListener != null) {
                    mUpdateListener.onWeatherInfoUpdateSuccess(mParser.parser(mResult, mCurrCity));
                }
                onLoadEnd();
                break;
            case LOAD_FAILED:
                if (mUpdateListener != null) {
                    mUpdateListener.onWeatherInfoUpdateFailed();
                }
                onLoadEnd();
                break;
            }
        }
    };

    private void onLoadEnd() {
        if (mLoading || mPendingList.isEmpty()) {
            return;
        }
        String next = mPendingList.get(0);
        mPendingList.remove(0);
        requestWeatherInfoUpdate(next);
    }

    public Client(Context context) {
        mContext = context;
        mParser = new WeatherInfoParser();
    }

    public void setUpdateListener(OnUpdateListener listener) {
        mUpdateListener = listener;
    }

    public void requestWeatherInfoUpdate(String city) {
        if (mLoading) {
            Log.i("wdb", "It's busy now and will doGet later automaticly");
            boolean contains = false;
            for (int i = 0; i < mPendingList.size(); i++) {
                if (mPendingList.get(i).equals(city)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                mPendingList.add(city);
            }
            return;
        }
        mLoading = true;
        mCurrCity = city;
        // new WeatherGetThread(city).start();
        Log.i(TAG, "start doGet " + city);
        doPostJuheSDK(city);
    }

    private void doPostJuheSDK(String city) {
        Parameters params = new Parameters();
        params.add("cityname", city);
        JuheData.executeWithAPI(mContext, WEATHER_JUHE_API_ID, WEATHER_URL_JUHE_SDK, JuheData.GET, params, new DataCallBack() {

            @Override
            public void onSuccess(int arg0, String arg1) {
                Log.i(TAG, "doPost onSuccess:" + arg1);
                mResult = arg1;
                mLoading = false;
                mHandler.sendEmptyMessage(LOAD_SUCCESS);
            }

            @Override
            public void onFinish() {
                mLoading = false;
                Log.i(TAG, "doPost onFinish");
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                Log.i(TAG, "doPost Error:" + arg1);
                mLoading = false;
                mHandler.sendEmptyMessage(LOAD_FAILED);
            }
        });
    }

    private boolean doPostJuhe(String city) {
        String url = WEATHER_URL_JUHE + city;
        return doPost(url);
    }

    private boolean doPost360(String city) {
        String url = WEATHER_URL_360 + city;
        return doPost(url);
    }

    private boolean doPost(String url) {
        HttpGet httpRequest = new HttpGet(url);
        String strResult = "doPostError";
        boolean success = false;
        try {
            HttpResponse httpResponse = getHttpClient().execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
                success = true;
            } else {
                strResult = "Error Response: " + httpResponse.getStatusLine().toString();
            }
        } catch (ClientProtocolException e) {
            strResult = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            strResult = e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            strResult = e.getMessage();
            e.printStackTrace();
        }
        Log.i(TAG, "doPost result:" + strResult);
        mResult = strResult;
        mLoading = false;
        if (success) {
            mHandler.sendEmptyMessage(LOAD_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(LOAD_FAILED);
        }
        return success;
    }

    private HttpClient getHttpClient() {
        if (mHttpClient == null) {
            BasicHttpParams httpParams = new BasicHttpParams();
            // 设置连接超时和 Socket 超时，以及 Socket 缓存大小
            HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            // 设置重定向，缺省为 true
            HttpClientParams.setRedirecting(httpParams, true);
            // 设置 user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);
            // 创建一个 HttpClient 实例
            // 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient
            // 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
            mHttpClient = new DefaultHttpClient(httpParams);
        }
        return mHttpClient;
    }
}
