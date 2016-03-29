package com.xlgzs.weather.cartoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xlgzs.weather.ICartoonDraw;
import com.xlgzs.weather.R;

public class CartoonCloudy implements ICartoonDraw {
    private static final int CLOUD_SPEED = 1;
    private int screenWidth;
    private int screenHeiht;

    private Bitmap mViewBg;
    private Context mContext;
    private Bitmap mCloud;
    private int[] mCloudX = new int[2];
    

    public CartoonCloudy(Context context) {
        mContext = context;
    }

    @Override
    public void loadBitmap() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.screenHeiht = metrics.heightPixels;
        this.screenWidth = metrics.widthPixels;
        mViewBg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_cloudy);
        mViewBg = Bitmap.createScaledBitmap(mViewBg, screenWidth, screenHeiht, true);
        mCloud = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cloud);
        float scale = mCloud.getWidth() * 1.0f / screenWidth;
        int height = mCloud.getHeight();
        mCloud = Bitmap.createScaledBitmap(mCloud, screenWidth, (int) (height / scale), true);
        mCloudX[0] = -mCloud.getWidth();
        mCloudX[1] = 0;
    }

    @Override
    public Bitmap buildCacheBitmap(int width, int height, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
        canvas.drawBitmap(mViewBg, null, rect, paint);
        canvas.drawBitmap(mCloud, mCloudX[0], 0, paint);
        canvas.drawBitmap(mCloud, mCloudX[1], 0, paint);
        return bitmap;
    }

    @Override
    public void recycle() {
        mViewBg.recycle();
        mCloud.recycle();
    }

    @Override
    public void doDraw(Canvas canvas, Paint paint, int width, int height) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
            canvas.drawBitmap(mViewBg, null, rect, paint);
            mCloudX[0] += CLOUD_SPEED;
            mCloudX[1] += CLOUD_SPEED;
            if (mCloudX[0] > screenWidth) {
                mCloudX[0] = mCloudX[1] - screenWidth;
            }
            if (mCloudX[1] > screenWidth) {
                mCloudX[1] = mCloudX[0] - screenWidth;
            }
            canvas.drawBitmap(mCloud, mCloudX[0], 0, paint);
            canvas.drawBitmap(mCloud, mCloudX[1], 0, paint);
        }
    }

    @Override
    public Rect getLockRect() {
        return null;
    }

}
