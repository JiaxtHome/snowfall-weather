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

public class CartoonSunny implements ICartoonDraw {
    private float mMillSize;
    private float mPointMarginLeft;
    private float mPointMarginTop;
    private float mScaledPointMarginLeft;
    private float mScaledPointMarginTop;
    private int screenWidth;
    private int screenHeiht;

    private Bitmap mWindPoint;
    private Bitmap mWindmill;
    private Bitmap mViewBg;
    private float mScaleX;
    private float mScaleY;
    private float mRotate;
    private Context mContext;

    public CartoonSunny(Context context) {
        mContext = context;
    }

    @Override
    public void loadBitmap() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.screenHeiht = metrics.heightPixels;
        this.screenWidth = metrics.widthPixels;
        mMillSize = mContext.getResources().getDimension(R.dimen.mill_size);
        mPointMarginLeft = mContext.getResources().getDimension(R.dimen.point_margin_left);
        mPointMarginTop = mContext.getResources().getDimension(R.dimen.point_margin_top);
        mViewBg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_sunny);
        mWindmill = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.na_windmill);
        mWindPoint = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.na_point);
        mWindmill = Bitmap.createScaledBitmap(mWindmill, (int) mMillSize, (int) mMillSize, true);
        mScaleX = mViewBg.getWidth() * 1.0f / screenWidth;
        mScaleY = mViewBg.getHeight() * 1.0f / screenHeiht;
        mViewBg = Bitmap.createScaledBitmap(mViewBg, screenWidth, screenHeiht, true);
        mScaledPointMarginLeft = mPointMarginLeft / mScaleX;
        mScaledPointMarginTop = mPointMarginTop / mScaleY;
    }

    @Override
    public Bitmap buildCacheBitmap(int width, int height, Paint paint) {
        int _dx = (int) mScaledPointMarginLeft;
        int _dy = (int) mScaledPointMarginTop;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
        canvas.drawBitmap(mViewBg, null, rect, paint);
        Matrix matrix = new Matrix();
        matrix.postRotate(mRotate % 360f, mWindmill.getWidth() / 2, mWindmill.getHeight() / 2);
        matrix.postTranslate(_dx - mWindmill.getWidth() / 2, _dy - (mWindmill.getHeight() / 2));
        canvas.drawBitmap(mWindmill, matrix, paint);
        canvas.drawBitmap(mWindPoint, _dx - mWindPoint.getWidth() / 2, _dy - mWindPoint.getHeight() / 2, paint);
        return bitmap;
    }

    @Override
    public void recycle() {
        mViewBg.recycle();
        mWindmill.recycle();
        mWindPoint.recycle();
    }

    @Override
    public void doDraw(Canvas canvas, Paint paint, int width, int height) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            RectF rect = new RectF(0, 0, screenWidth, screenHeiht);
            canvas.drawBitmap(mViewBg, null, rect, paint);
            Matrix matrix = new Matrix();
            matrix.postRotate((mRotate += 0.5) % 360f, mWindmill.getWidth() / 2, mWindmill.getHeight() / 2);
            matrix.postTranslate(mScaledPointMarginLeft - mWindmill.getWidth() / 2, mScaledPointMarginTop - (mWindmill.getHeight() / 2));
            canvas.drawBitmap(mWindmill, matrix, paint);
            canvas.drawBitmap(mWindPoint, mScaledPointMarginLeft - mWindPoint.getWidth() / 2, mScaledPointMarginTop - mWindPoint.getHeight() / 2, paint);

        }
    }

    @Override
    public Rect getLockRect() {
        Rect millRec = new Rect((int) (mScaledPointMarginLeft - mWindmill.getWidth() / 2), (int) (mScaledPointMarginTop - (mWindmill.getHeight() / 2)),
                (int) (mScaledPointMarginLeft + mWindmill.getWidth() / 2), (int) (mScaledPointMarginTop + (mWindmill.getHeight() / 2)));
        Rect lockRec = getOutterRect(millRec);
        return lockRec;
    }

    private Rect getOutterRect(Rect inner) {
        int d = (int) ((inner.width() / Math.cos(45) - inner.width()) / 2);
        return new Rect(inner.left - d, inner.top - d, inner.right + d, inner.bottom + d);
    }
}
