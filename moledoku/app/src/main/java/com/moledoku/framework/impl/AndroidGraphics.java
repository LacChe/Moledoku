package com.moledoku.framework.impl;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.Window;

import com.moledoku.framework.Graphics;
import com.moledoku.framework.Pixmap;

import java.io.IOException;
import java.io.InputStream;

public class AndroidGraphics implements Graphics {

    AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    RectF dstRect = new RectF();

    float scale;

    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();

        this.scale = (float)frameBuffer.getWidth() / 192.0f;
    }

    public Pixmap newPixmap(String fileName, PixmapFormat format) {
        Config config = null;
        if (format == PixmapFormat.RGB565)
            config = Config.RGB_565;
        else if (format == PixmapFormat.ARGB4444)
            config = Config.ARGB_4444;
        else
            config = Config.ARGB_8888;
        Options options = new Options();
        options.inPreferredConfig = config;
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }
        if (bitmap.getConfig() == Config.RGB_565)
            format = PixmapFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = PixmapFormat.ARGB4444;
        else
            format = PixmapFormat.ARGB8888;
        return new AndroidPixmap(bitmap, format);
    }

    public void clear(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    public void drawPixel(float x, float y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    public void drawLine(float x, float y, float x2, float y2, int color, float w) {
        paint.setColor(color);
        paint.setStrokeWidth(w);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    public void drawRect(float x, float y, float width, float height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void drawPixmap(Pixmap pixmap, float x, float y, float srcX, float srcY,
                           float srcWidth, float srcHeight) {
        srcRect.left = (int)srcX;
        srcRect.top = (int)srcY;
        srcRect.right = (int)(srcX + srcWidth);
        srcRect.bottom = (int)(srcY + srcHeight);
        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;
        Paint p = new Paint();
        p.setAntiAlias(false);
        p.setDither(false);
        p.setFilterBitmap(false);
        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, p);
    }

    public void drawPixmap(Pixmap pixmap, float x, float y, float width, float height, float srcX, float srcY,
                           float srcWidth, float srcHeight) {
        srcRect.left = (int)srcX;
        srcRect.top = (int)srcY;
        srcRect.right = (int)(srcX + srcWidth);
        srcRect.bottom = (int)(srcY + srcHeight);
        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + width;
        dstRect.bottom = y + height;
        Paint p = new Paint();
        p.setAntiAlias(false);
        p.setDither(false);
        p.setFilterBitmap(false);
        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, p);
    }

    public void drawPixmap(Pixmap pixmap, float x, float y) {
        Paint p = new Paint();
        p.setAntiAlias(false);
        p.setDither(false);
        p.setFilterBitmap(false);
        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, p);
    }

    public int getWidth() {
        return frameBuffer.getWidth();
    }

    public int getHeight() {
        return frameBuffer.getHeight();
    }

    public float getScale() {
        return scale;
    }

}