package com.xlgzs.weather;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xlgzs.weather.city.CityModel;
import com.xlgzs.weather.city.DistrictModel;
import com.xlgzs.weather.city.ProvinceModel;
import com.xlgzs.weather.city.XmlParserHandler;

public class CityPanel extends RelativeLayout implements View.OnClickListener {

    private static final int LOAD_COMPLETE = 1;
    private AutoCompleteTextView mAuto;
    protected List<String> mProvinceDatas = new ArrayList<String>();
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    private Context mContext;
    private ImageView mBack;
    private int mScreenWidth;
    private List<String> mData = new ArrayList<String>();
    private CityManager mCityManager;
    private LinearLayout mHotCityContainer;
    private HotCitySelector mCitySelector;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_COMPLETE) {
                initCompleteData();
                mAuto.setEnabled(true);
            }
        }
    };

    class LoadDataThread extends Thread {

        @Override
        public void run() {
            loadProvinceDatas();
            mHandler.sendEmptyMessage(LOAD_COMPLETE);
        }
    }

    interface OnCityAddCallback {
        void onCityAdd(City city);
    }

    OnItemClickListener mItemSelectListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String select = ((TextView) arg1).getText().toString().replaceAll("[市区县][[\\(][\\s\\S]*[\\)]]*", "");
            boolean contains = mCityManager.contains(select);
            if (!contains) {
                City city = new City();
                city.name = select;
                city.index = mCityManager.getCityCount();
                mCityManager.addCity(city);
                if (mCallback != null) {
                    mCallback.onCityAdd(city);
                }
                dismiss();
            } else {
                Toast.makeText(mContext, R.string.add_city_fail, Toast.LENGTH_SHORT).show();
            }
        }
    };

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                mHotCityContainer.setVisibility(View.VISIBLE);
            } else {
                mHotCityContainer.setVisibility(View.GONE);
            }
        }
    };

    private OnCityAddCallback mCallback;

    public CityPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CityPanel(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
    }

    public void setCityManager(CityManager cityMgr) {
        mCityManager = cityMgr;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = inflate(mContext, R.layout.city_panel, this);
        mAuto = (AutoCompleteTextView) root.findViewById(R.id.autoCompleteTextView1);
        mAuto.setOnItemClickListener(mItemSelectListener);
        mHotCityContainer = (LinearLayout) root.findViewById(R.id.hotCityContainer);
        mAuto.addTextChangedListener(mTextWatcher);
        mBack = (ImageView) findViewById(R.id.cityBack);
        mBack.setOnClickListener(this);
        mCitySelector = (HotCitySelector) root.findViewById(R.id.cityGird);
        mCitySelector.setOnItemClickListener(mItemSelectListener);
    }

    public void setSelectCallback(OnCityAddCallback callback) {
        mCallback = callback;
    }

    private void initCompleteData() {
        for (int i = 0; i < mProvinceDatas.size(); i++) {
            String pro = mProvinceDatas.get(i);
            for (int j = 0; j < mCitisDatasMap.get(pro).length; j++) {
                String city = mCitisDatasMap.get(pro)[j];
                mData.add(city);
                for (int k = 0; k < mDistrictDatasMap.get(city).length; k++) {
                    mData.add(mDistrictDatasMap.get(city)[k] + "(" + city + ")");
                }
            }
        }
        ArrayAdapter<String> av = new ArrayAdapter<String>(mContext, R.layout.city_search_item, mData);
        mAuto.setAdapter(av);
    }

    private void loadProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();
            for (int i = 0; i < provinceList.size(); i++) {
                mProvinceDatas.add(provinceList.get(i).getName());
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void show() {
        final float width = mScreenWidth;
        ValueAnimator va = ValueAnimator.ofFloat(1, 0f);
        va.setDuration(300);
        va.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                setTranslationX(-width * value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                onShowEnd();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onShowEnd();
            }
        });
        va.start();
    }

    private void onShowEnd() {
        new LoadDataThread().start();
    }

    public void dismiss() {
        final float width = mScreenWidth;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1f);
        va.setDuration(300);
        va.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                setTranslationX(-width * value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                onDismissEnd();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onDismissEnd();
            }
        });
        va.start();
    }

    private void onDismissEnd() {
        setVisibility(View.GONE);
        mAuto.setText("");
        releaseData();
    }

    @Override
    public void onClick(View v) {

        if (v == mBack) {
            dismiss();
        }
    }

    private void releaseData() {
        mData.clear();
        mCitisDatasMap.clear();
        mDistrictDatasMap.clear();
        mProvinceDatas.clear();
        mAuto.setEnabled(false);
    }
}
