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

public class CartoonSunset implements ICartoonDraw {
    private static final int BIRD_SPEED = 1;
    private int screenWidth;
    private int screenHeiht;
    private int[] mBirdMarginTop = new int[2];

    private Bitmap mViewBg;
    private Context mContext;
    private Bitmap[] mBirds = new Bitmap[8];
    private float mFrame;
    private int mTop;
    private int mBirdX;

    public CartoonSunset(Context context) {
        mContext = context;
    }

    @Override
    public void loadBitmap() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.screenHeiht = metrics.heightPixels;
        this.screenWidth = metrics.widthPixels;
        mBirdMarginTop[0] = (int) (screenHeiht * 0.15f);
        mBirdMarginTop[1] = (int) (screenHeiht * 0.3f);
        mViewBg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_sunset);
        mViewBg = Bitmap.createScaledBitmap(mViewBg, screenWidth, screenHeiht, true);
        for (int i = 0; i < 8; i++) {
            mBirds[i] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.finedayup_1 + i);
        }
        mBirdX = -mBirds[0].getWidth();
    }

    @Override
    public Bitmap buildCacheBitmap(int width, int height, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
        canvas.drawBitmap(mViewBg, null, rect, paint);
        int frame = ((int) mFrame) % 8;
        canvas.drawBitmap(mBirds[frame], mBirdX, mBirdMarginTop[mTop], paint);
        return bitmap;
    }

    @Override
    public void recycle() {
        mViewBg.recycle();
        for (int i = 0; i < 8; i++) {
            mBirds[i].recycle();
        }
    }

    @Override
    public void doDraw(Canvas canvas, Paint paint, int width, int height) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
            canvas.drawBitmap(mViewBg, null, rect, paint);
            mFrame += 0.1f;
            int frame = ((int) mFrame) % 8;
            if (mBirdX > screenWidth) {
                mBirdX = -mBirds[0].getWidth();
                mTop = (mTop + 1) % 2;
            }
            canvas.drawBitmap(mBirds[frame], mBirdX += BIRD_SPEED, mBirdMarginTop[mTop], paint);
        }
    }

    @Override
    public Rect getLockRect() {
        return null;
    }

}
