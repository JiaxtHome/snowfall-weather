package com.xlgzs.weather.cartoon;

import java.util.Random;
import com.xlgzs.weather.ICartoonDraw;
import com.xlgzs.weather.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class CartoonRainy implements ICartoonDraw {
    private static final int MAX_SNOW_COUNT = 40;
    private static final int MAX_SPEED = 45;
    private static final int MIN_SPEED = 25;
    private static final int RAIN_MARGIN_BOTTOM = 20;
    private int screenWidth;
    private int screenHeiht;

    private Bitmap mRainDrop;
    private Bitmap mViewBg;
    private Context mContext;
    private static final Random random = new Random();
    private Rain[] mRains = new Rain[MAX_SNOW_COUNT];
    private int mRainDropHeight;

    public CartoonRainy(Context context) {
        mContext = context;
    }

    @Override
    public void loadBitmap() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.screenHeiht = metrics.heightPixels;
        this.screenWidth = metrics.widthPixels;
        mViewBg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_night);
        mRainDrop = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.raindrop_l);
        mRainDropHeight = mRainDrop.getHeight();
        mViewBg = Bitmap.createScaledBitmap(mViewBg, screenWidth, screenHeiht, true);
        for (int i = 0; i < MAX_SNOW_COUNT; i++) {
            mRains[i] = new Rain(random.nextInt(screenWidth), 0, random.nextInt(MAX_SPEED));
        }
    }

    @Override
    public Bitmap buildCacheBitmap(int width, int height, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
        canvas.drawBitmap(mViewBg, null, rect, paint);
        for (int i = 0; i < MAX_SNOW_COUNT; i++) {
            if (mRains[i].x >= width || mRains[i].y >= height) {
                mRains[i].y = 0;
                mRains[i].x = random.nextInt(width);
            }
            canvas.drawBitmap(mRainDrop, mRains[i].x, ((float) mRains[i].y), paint);
        }
        return bitmap;
    }

    @Override
    public void recycle() {
        mViewBg.recycle();
        mRainDrop.recycle();
    }

    @Override
    public void doDraw(Canvas canvas, Paint paint, int width, int height) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
            canvas.drawBitmap(mViewBg, null, rect, paint);
            for (int i = 0; i < MAX_SNOW_COUNT; i++) {
                if (mRains[i].x >= width || mRains[i].y >= height - mRainDropHeight - RAIN_MARGIN_BOTTOM) {
                    mRains[i].y = 0;
                    mRains[i].x = random.nextInt(width);
                }
                mRains[i].y += mRains[i].speed + MIN_SPEED;
                canvas.drawBitmap(mRainDrop, mRains[i].x, ((float) mRains[i].y), paint);
            }
        }
    }

    @Override
    public Rect getLockRect() {
        return null;
    }

    class Rain {
        int speed;
        int x;
        int y;

        public Rain(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            if (this.speed == 0) {
                this.speed = 1;
            }
        }

    }
}
