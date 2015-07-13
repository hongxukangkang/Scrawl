package com.meitu.utils;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by mtdiannao on 2015/6/17.
 */
public class TextureUtils {

    /*****
     * generate a texture with the specified color
     * @param x
     * @param y
     * @param color
     * @return
     */
    public static int generateNewTexture(int x, int y, int color) {
        IntBuffer intBuffer = ByteBuffer.allocateDirect(x * y * 4).asIntBuffer();
         for (int i = 0; i < x * y; i++) {
            intBuffer.put(color);
        }
        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, x, y, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId[0];
    }
}