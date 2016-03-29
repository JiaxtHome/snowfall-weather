package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xlgzs.weather.ViewPager.OnPageChangeListener;

public class PageContainer extends FrameLayout implements OnPageChangeListener {

    private Context mContext;
    private ViewPager mViewPager;
    private VerticalFragementPagerAdapter mAdapter;
    private List<Page> mPages = new ArrayList<Page>();
    private int mSelected;
    private int mOrgSelected;
    private ScrimView mScrim;
    private boolean mSlidDetermined = false;
    private float mDownX, mDownY;
    private float mMoveX, mMoveY;
    private boolean isSlidingHorizentally = false;

    interface PageChangedCallback {
        void onPageChanged(int index);
    }

    private PageChangedCallback mCallback;

    public PageContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new VerticalFragementPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    public void setScrimView(ScrimView view) {
        mScrim = view;
    }

    public void onChildPagePanelChanged(int index) {
        if (index == 0 && mScrim != null) {
            mScrim.dismiss();
        } else if (index == 1 && mScrim != null) {
            mScrim.show();
        }
        for (int i = 0; i < mPages.size(); i++) {
            mPages.get(i).setSelectedItemImmediately(index);
        }
    }

    public void addPages(int count) {
        mPages.clear();
        for (int i = 0; i < count; i++) {
            Page page = (Page) LayoutInflater.from(mContext).inflate(R.layout.page, null);
            page.setParentContainer(this);
            mPages.add(page);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void addPage() {
        Page page = (Page) LayoutInflater.from(mContext).inflate(R.layout.page, null);
        page.setParentContainer(this);
        mPages.add(page);
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mPages.size() - 1);
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
            if (mCallback != null && mOrgSelected != mSelected) {
                mCallback.onPageChanged(mSelected);
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

    public int getSize() {
        return mPages.size();
    }

    public Page getPage(int index) {
        return mPages.get(index);
    }

    public void setPageChangedCallback(PageChangedCallback callback) {
        mCallback = callback;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDownX = ev.getX();
            mDownY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            mMoveX = ev.getX();
            mMoveY = ev.getY();
            if (!mSlidDetermined) {
                mSlidDetermined = true;
                float x = mMoveX - mDownX;
                float y = mMoveY - mDownY;
                if (Math.abs(x) > Math.abs(y)) {
                    isSlidingHorizentally = true;
                    return true;
                }
            }
            if (isSlidingHorizentally) {
                mViewPager.onTouchEvent(ev);
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mSlidDetermined = false;
            isSlidingHorizentally = false;
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public Page getCurrentPage() {
        return mPages.get(mViewPager.getCurrentItem());
    }

    public int getCurrentIndex() {
        return mViewPager.getCurrentItem();
    }

    public void setCurrentPage(int index) {
        if (index < mPages.size()) {
            mViewPager.setCurrentItem(index, false);
            mOrgSelected = index;
        }
    }
}
