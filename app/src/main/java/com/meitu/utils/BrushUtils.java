package com.meitu.utils;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by mtdiannao on 2015/7/8.
 */
public class BrushUtils {

    /**生成一个使用特定颜色的画笔，返回一个纹理**/
    /**
     * generate a brush with the specified color value,return a texture**
     */
    public static int generateSpecifiedBrush(int[] color) {

        IntBuffer colorBuffer = ByteBuffer.allocateDirect(color.length * RGBAGLSurfaceViewParameters.BYTES_PER_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
        colorBuffer.put(color).position(0);
        int textureId;
        int[] textureBuff = new int[1];
        GLES20.glGenTextures(1, textureBuff, 0);
        textureId = textureBuff[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, 32, 32, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_INT,colorBuffer);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId;
    }

    /**生成一个使用特定图像的画笔，返回一个纹理**/
    /**
     * generate a brush with the specified color value,return a texture**
     */
    public static int generateImageBrush(Bitmap bitmap) {
//        GLES20.glColorMask();
        int textureId;
        int[] textureBuff = new int[1];
        GLES20.glGenTextures(1, textureBuff, 0);
        textureId = textureBuff[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, 32, 32, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_INT, colorBuffer);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId;
    }
}