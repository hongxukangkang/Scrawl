package com.meitu.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by mtdiannao on 2015/6/30.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * @param mBitmap
     * @param width
     * @param height
     * @return
     */
    public static RectF getImageRectF(Bitmap mBitmap, int width, int height) {
        RectF rect = new RectF();

        float glHalfWidth = 1.0f;
        float glHalfHeight = 1.0f;

        int mCurrentBitmapWidth = mBitmap.getWidth();
        int mCurrentBitmapHeight = mBitmap.getHeight();

        float imageAspect = mCurrentBitmapWidth / (float) mCurrentBitmapHeight;
        float renderBufferAspect = width / (float) height;

        if (imageAspect > renderBufferAspect) {
            glHalfWidth = Math.min(1.0f, mCurrentBitmapWidth / (float) width);
            glHalfHeight = glHalfWidth * renderBufferAspect / imageAspect;
        } else {
            glHalfHeight = Math.min(1.0f, mCurrentBitmapHeight / (float) height);
            glHalfWidth = imageAspect / renderBufferAspect;
        }
        Log.i(TAG, "glHalfWidth:" + glHalfWidth);
        Log.i(TAG, "glHalfHeight:" + glHalfHeight);
        rect.set(-glHalfWidth, glHalfHeight, glHalfWidth, -glHalfHeight);
        return rect;
    }

    /**
     *
     */
    public static Bitmap rotateImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);        /**/
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBitmap;
    }

    /**
     * **
     */
    public static Bitmap revertImage(Bitmap bmp) {
        float[] floats = {1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f};
        Matrix matrix = new Matrix();
        matrix.setValues(floats);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    /****
     * get the IntBuffer of the image's bitmap
     * @param bitmap
     * @return
     */
    public static IntBuffer getBufferFromImage(Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int []result = new int[width * height];
        bitmap.getPixels(result,0,width,0,0,width,height);
        IntBuffer intBuffer = ByteBuffer.allocateDirect(result.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        intBuffer.put(result).position(0);
        return  intBuffer;
    }
}
