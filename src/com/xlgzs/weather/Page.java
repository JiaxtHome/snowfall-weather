package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xlgzs.weather.ViewPager.OnPageChangeListener;

public class Page extends FrameLayout implements OnPageChangeListener {

    private Context mContext;
    private ViewPager mViewPager;
    private VerticalFragementPagerAdapter mAdapter;
    private List<View> mPages = new ArrayList<View>();
    private TodayPanel mToday;
    private WeekPanel mWeek;
    private int mSelected;
    private int mOrgSelected;
    private PageContainer mParentContainer;
    private WeatherInfo mWeather;

    public Page(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public Page(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Page(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewPager = (ViewPager) findViewById(R.id.pager_view);
        mToday = (TodayPanel) LayoutInflater.from(mContext).inflate(R.layout.today_panel, null);
        mPages.add(mToday);
        mWeek = (WeekPanel) LayoutInflater.from(mContext).inflate(R.layout.week_panel, null);
        mPages.add(mWeek);
        mAdapter = new VerticalFragementPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    public void setParentContainer(PageContainer parent) {
        mParentContainer = parent;
    }

    private class VerticalFragementPagerAdapter extends ViewPagerAdapter {

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(mPages.get(position));
            return mPages.get(position);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 0) {
            if (mParentContainer != null && mOrgSelected != mSelected) {
                mParentContainer.onChildPagePanelChanged(mSelected);
            }
            mOrgSelected = mSelected;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        mSelected = arg0;
    }

    public void setInfo(WeatherInfo info) {
        mWeather = info;
        mToday.setInfo(info);
        mWeek.setInfo(info);
    }

    public void setSelectedItemImmediately(int index) {
        if (mViewPager.getCurrentItem() != index) {
            mViewPager.setCurrentItem(index, false);
            mSelected = index;
            mOrgSelected = index;
        }
    }

    public WeatherInfo getWeather() {
        return mWeather;
    }

}
