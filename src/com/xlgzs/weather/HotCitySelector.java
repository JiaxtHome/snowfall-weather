package com.xlgzs.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class HotCitySelector extends GridView {

    private Context mContext;
    private List<String> data = new ArrayList<String>();
    private ArrayAdapter mAdapter;

    public HotCitySelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public HotCitySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HotCitySelector(Context context) {
        super(context);
        init(context);
    }

    @SuppressWarnings("unchecked")
    private void init(Context context) {
        mContext = context;
        String[] hotCity = context.getResources().getStringArray(R.array.hotCity);
        for (int i = 0; i < hotCity.length; i++) {
            data.add(hotCity[i]);
        }
        mAdapter = new ArrayAdapter(mContext, R.layout.hot_city_item, data);
        setAdapter(mAdapter);
    }

    public void fresh() {
        mAdapter.notifyDataSetChanged();
    }
}
