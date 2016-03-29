package com.xlgzs.weather;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitlePanel extends RelativeLayout implements View.OnClickListener {

    private TextView mCity;
    private TextView mDate;
    private PageIndicator mIndicator;
    private SimpleDateFormat mSdf;
    private Context mContext;
    private ImageView mShare;

    public TitlePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitlePanel(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSdf = new SimpleDateFormat("M月d日  EE", Locale.CHINA);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = inflate(mContext, R.layout.title_panel, this);
        mCity = (TextView) root.findViewById(R.id.city);
        mDate = (TextView) root.findViewById(R.id.date);
        mIndicator = (PageIndicator) root.findViewById(R.id.indicator);
        mShare = (ImageView) root.findViewById(R.id.share);
        // mShare.setOnClickListener(this);
    }

    public void setInfo(City city) {
        mCity.setText(city.name);
        String date = mSdf.format(new Date(System.currentTimeMillis()));
        mDate.setText(date);
        mIndicator.toPage(city.index);
    }

    public void initPages(int count) {
        mIndicator.initPages(count);
    }

    @Override
    public void onClick(View v) {
        if (v == mShare) {
            Intent recorder = new Intent(mContext, RecorderActivity.class);
            recorder.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(recorder);
        }
    }

    public void addCity(String name) {
        mIndicator.addOne();
        mCity.setText(name);
    }

}
