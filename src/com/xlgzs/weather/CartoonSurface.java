package com.xlgzs.weather;

import com.xlgzs.weather.cartoon.CartoonCloudy;
import com.xlgzs.weather.cartoon.CartoonRainy;
import com.xlgzs.weather.cartoon.CartoonStarry;
import com.xlgzs.weather.cartoon.CartoonSunny;
import com.xlgzs.weather.cartoon.CartoonSunset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class CartoonSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "surface";
    private SurfaceHolder holder;
    private static final int INTERVAL = 16;
    private boolean isRunning = true;
    private Paint mPaint;
    private ICartoonDraw mCartoon;
    private Context mContext;

    public CartoonSurface(Context context) {
        super(context);
        init(context);
    }

    public CartoonSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CartoonSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    @Override
    public void run() {

        long nextTime = System.currentTimeMillis();
        Canvas canvas = null;
        while (isRunning) {
            synchronized (this) {
                try {
                    canvas = holder.lockCanvas(/* mCartoon.getLockRect() */);
                    int width = getRight() - getLeft();
                    int height = getBottom() - getTop();
                    mCartoon.doDraw(canvas, mPaint, width, height);
                    long vert = System.currentTimeMillis() - nextTime;
                    if (vert < INTERVAL) {
                        Thread.sleep(INTERVAL - vert);
                    }
                    nextTime = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public void setRunning(boolean state) {
        isRunning = state;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.i(TAG, "changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i(TAG, "created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i(TAG, "destroyed");
        isRunning = false;
    }

    public Bitmap getCacheBitmap() {
        if (mCartoon == null) {
            return null;
        }
        int width = getRight() - getLeft();
        int height = getBottom() - getTop();
        return mCartoon.buildCacheBitmap(width, height, mPaint);
    }

    public void start() {
        isRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        isRunning = false;
    }

    public void onWeatherChanged(int weather) {
        mCartoon = getCartoonDraw(weather);
        mCartoon.loadBitmap();
    }

    private ICartoonDraw getCartoonDraw(int weather) {
        Log.i("jxt", "cartoon = " + weather);
        ICartoonDraw cartoon = null;
        switch (weather) {
        case 0:
            if (Utils.isNight()) {
                cartoon = new CartoonStarry(mContext);
            } else {
                cartoon = new CartoonSunny(mContext);
            }
            break;
        case 1:
            cartoon = new CartoonSunset(mContext);
            break;
        case 2:
            cartoon = new CartoonCloudy(mContext);
            break;
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
            cartoon = new CartoonRainy(mContext);
            break;
        default:
            cartoon = new CartoonSunset(mContext);
            break;
        }
        return cartoon;
    }
}
