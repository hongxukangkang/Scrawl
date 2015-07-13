package com.meitu.widgetObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.meitu.android.R;
import com.meitu.utils.CommonParameters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mtdiannao on 2015/6/25.
 */
public class ImageRegion {

    private static final String TAG = "ImageRegion";
    private static final int BYTES_PER_FLOAT = 4;

    private float[] vertex = {
            -1.0f, -0.5f,//left bottom;
             1.0f, -0.5f,//right bottom;
            -1.0f,  0.5f,//left top
             1.0f,  0.5f//right top
    };

    private float[] textureCord = {
            0.0f, 1.0f,//left top
            1.0f, 1.0f,//right top
            0.0f, 0.0f,//left bottom
            1.0f, 0.0f //right bottom
    };

    private Bitmap backgroundBitmap;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCordBuffer;

    private int mProgramId;
    private int mTextureId;

    private int positionHandle;
    private int textureCordHandle;
    private int samplerHandle;

    public ImageRegion(Context context,int programId,int textureId){//GLES20
        this.mProgramId = programId;
        this.mTextureId = textureId;
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pomelo_test);

        positionHandle = GLES20.glGetAttribLocation(mProgramId, CommonParameters.A_POSITION);
        textureCordHandle = GLES20.glGetAttribLocation(mProgramId, CommonParameters.A_TEXTURECOORD);
        samplerHandle = GLES20.glGetUniformLocation(mProgramId, CommonParameters.U_SAMPLE);

        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertex).position(0);

        textureCordBuffer = ByteBuffer.allocateDirect(textureCord.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCordBuffer.put(textureCord).position(0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, backgroundBitmap, 0);
        GLES20.glUniform1f(samplerHandle, 1.0f);

    }

    public void drawRegion() {

        GLES20.glUseProgram(mProgramId);
        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle,2, GLES20.GL_FLOAT,false,0,vertexBuffer);

        GLES20.glEnableVertexAttribArray(textureCordHandle);
        GLES20.glVertexAttribPointer(textureCordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}