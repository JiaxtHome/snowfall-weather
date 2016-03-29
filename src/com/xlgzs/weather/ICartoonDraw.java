package com.xlgzs.weather;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public interface ICartoonDraw {

    void loadBitmap();

    void recycle();

    void doDraw(Canvas canvas, Paint paint, int width, int height);

    Rect getLockRect();

    Bitmap buildCacheBitmap(int width, int height, Paint paint);
}
