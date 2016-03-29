package com.xlgzs.weather;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class GpsHelper {
    private static final String TAG = "gps";
    private LocationClient mLocationClient = null;
    private Context mContext;
    private BDLocationListener mListener = new MyLocationListener();
    private GpsCallback mCallback;

    interface GpsCallback {
        void onLocationResult(String cityname);
    }

    public GpsHelper(Context context, GpsCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void gpsStart() {
        Log.i(TAG, "gps started");
        mLocationClient = new LocationClient(mContext); // ����LocationClient��
        mLocationClient.registerLocationListener(mListener); // ע�������
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// ��GPS
        option.setAddrType("all");// ���صĶ�λ�����ַ��Ϣ
        option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
        option.setScanSpan(-1);// ���÷���λ����ļ��ʱ��Ϊ3000ms
        option.disableCache(false);// ��ֹ���û��涨λ
        option.setPriority(LocationClientOption.NetWorkFirst);// ���綨λ����
        mLocationClient.setLocOption(option);// ʹ������
        mLocationClient.start();// ������λSDK
        mLocationClient.requestLocation();// ��ʼ����λ��
    }

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            String cityName = null;
            Log.i(TAG, "gps result location = " + location);
            if (location != null) {
                StringBuffer sb = new StringBuffer(128);// ���ܷ��񷵻صĻ�����
                sb.append(location.getCity());// ��ó���
                cityName = sb.toString().trim().replaceAll("[市区县]", "");
            }
            if (mCallback != null) {
                mCallback.onLocationResult(cityName);
            }
            mLocationClient.stop();
        }

        @Override
        public void onReceivePoi(BDLocation arg0) {

        }

    }
}
