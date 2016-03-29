package com.xlgzs.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DiagramView extends View {

    private Context mContext;
    private float mMarginV;
    private float mDivMax;
    private float mDotRadius;
    private float mTextSize;
    private int[] mTempMaxs = new int[6];
    private int[] mTempMins = new int[6];
    private float[] mX = new float[6];
    private float mMarginExtra;
    private Paint mPaint;
    private float[] mUpYPix = new float[6];
    private float[] mDownYPix = new float[6];
    private boolean mDataSet = false;

    public DiagramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DiagramView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mDivMax = mContext.getResources().getDimension(R.dimen.diagram_div_max);
        mMarginV = mContext.getResources().getDimension(R.dimen.diagram_margin_verticle);
        mDotRadius = mContext.getResources().getDimension(R.dimen.diagram_dot_radius);
        mTextSize = mContext.getResources().getDimension(R.dimen.diagram_text_size);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(mTextSize);
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mDataSet) {
            return;
        }
        cacul(mTempMaxs, mTempMins);
        for (int i = 0; i < mX.length; i++) {
            canvas.drawCircle(mX[i], mUpYPix[i], mDotRadius, mPaint);
            canvas.drawText(mTempMaxs[i] + "°C", mX[i] - mTextSize, mUpYPix[i] - mTextSize, mPaint);
            canvas.drawCircle(mX[i], mDownYPix[i], mDotRadius, mPaint);
            canvas.drawText(mTempMins[i] + "°C", mX[i] - mTextSize, mDownYPix[i] + mTextSize * 2, mPaint);
        }

        for (int i = 1; i < mX.length; i++) {
            canvas.drawLine(mX[i - 1], mUpYPix[i - 1], mX[i], mUpYPix[i], mPaint);
            canvas.drawLine(mX[i - 1], mDownYPix[i - 1], mX[i], mDownYPix[i], mPaint);
        }
    }

    public void updataData(int[] upData, int[] downData) {
        mDataSet = true;
        mTempMaxs = upData;
        mTempMins = downData;
        invalidate();
    }

    private void cacul(int[] upY, int[] downY) {
        float maxY = 0;
        for (int i = 0; i < upY.length; i++) {
            if (maxY < upY[i]) {
                maxY = upY[i];
            }
        }
        float minY = downY[0];
        for (int i = 0; i < downY.length; i++) {
            if (minY > downY[i]) {
                minY = downY[i];
            }
        }
        float height = getMeasuredHeight();
        float div = (height - mMarginV * 2) / (maxY - minY);
        if (div > mDivMax) {
            mMarginExtra = (maxY - minY) * (div - mDivMax) / 2;
            div = mDivMax;
        }
        for (int i = 0; i < mX.length; i++) {
            float dy = (upY[i] - minY) * div + mMarginExtra + mMarginV;
            mUpYPix[i] = height - dy;
            dy = (downY[i] - minY) * div + mMarginExtra + mMarginV;
            mDownYPix[i] = height - dy;
        }
        float width = getMeasuredWidth();
        float colWidth = width / 6.0f;
        for (int i = 0; i < 6; i++) {
            mX[i] = colWidth * (i + 1) - colWidth / 2;
        }
    }

}
