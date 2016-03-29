package com.xlgzs.weather.cartoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xlgzs.weather.ICartoonDraw;
import com.xlgzs.weather.R;

public class CartoonStarry implements ICartoonDraw {
    private static final int STAR_SPEED = 2;
    private int screenWidth;
    private int screenHeiht;

    private Bitmap mViewBg;
    private Context mContext;
    private Bitmap mStar;
    private int[] mStarX = new int[2];
    private int[] mStarY = new int[2];
    private Paint mPaint;
    private int mAlpha = 0;
    private boolean mIncreasing = true;
    private float mRotate;
    private Matrix matrix0 = new Matrix();
    private Matrix matrix1 = new Matrix();

    public CartoonStarry(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    @Override
    public void loadBitmap() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.screenHeiht = metrics.heightPixels;
        this.screenWidth = metrics.widthPixels;
        mViewBg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_star_night);
        mViewBg = Bitmap.createScaledBitmap(mViewBg, screenWidth, screenHeiht, true);
        mStar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.night_star_m);
        mStar = Bitmap.createScaledBitmap(mStar, mStar.getWidth() * 2, mStar.getHeight() * 2, true);
        mStarX[0] = (int) (screenWidth * 0.3f);
        mStarX[1] = (int) (screenWidth * 0.7f);
        mStarY[0] = (int) (screenHeiht * 0.15f);
        mStarY[1] = (int) (screenHeiht * 0.3f);
    }

    @Override
    public Bitmap buildCacheBitmap(int width, int height, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
        canvas.drawBitmap(mViewBg, null, rect, paint);
        canvas.drawBitmap(mStar, matrix0, mPaint);
        canvas.drawBitmap(mStar, matrix1, mPaint);
        return bitmap;
    }

    @Override
    public void recycle() {
        mViewBg.recycle();
        mStar.recycle();
    }

    @Override
    public void doDraw(Canvas canvas, Paint paint, int width, int height) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
            canvas.drawBitmap(mViewBg, null, rect, paint);

            if (mIncreasing) {
                mAlpha += STAR_SPEED;
                if (mAlpha >= 255) {
                    mAlpha = 255;
                    mStarX[1] = (int) (Math.random() * screenWidth);
                    mStarY[1] = (int) (Math.random() * 4 / 10 * screenHeiht);
                    mIncreasing = false;
                }
            } else {
                mAlpha -= STAR_SPEED;
                if (mAlpha <= 0) {
                    mAlpha = 0;
                    mStarX[0] = (int) (Math.random() * screenWidth);
                    mStarY[0] = (int) (Math.random() * 4 / 10 * screenHeiht);
                    mIncreasing = true;
                }
            }
            matrix0.reset();
            matrix0.postRotate((mRotate += 0.3) % 360f, mStar.getWidth() / 2, mStar.getHeight() / 2);
            matrix0.postTranslate(mStarX[0], mStarY[0]);
            mPaint.setAlpha(mAlpha);
            canvas.drawBitmap(mStar, matrix0, mPaint);
            mPaint.setAlpha(255 - mAlpha);
            matrix1.reset();
            matrix1.postRotate(360 - ((mRotate += 0.5) % 360f), mStar.getWidth() / 2, mStar.getHeight() / 2);
            matrix1.postTranslate(mStarX[1], mStarY[1]);
            canvas.drawBitmap(mStar, matrix1, mPaint);
        }
    }

    @Override
    public Rect getLockRect() {
        return null;
    }

}
