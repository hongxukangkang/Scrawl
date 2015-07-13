package com.meitu.widgetObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.util.Log;

import com.meitu.utils.BufferUtils;
import com.meitu.utils.CommonParameters;
import com.meitu.utils.ImageUtils;
import com.meitu.utils.ShaderUtils;

import java.nio.FloatBuffer;

/**
 * Created by mtdiannao on 2015/7/1.
 */
public class Square {

    private static final String TAG = "Square";

    private int mWidth;
    private int mHeight;
    private int program;
    private Context mContext;
    private RectF imgShowDomain;

    private int textureHandle;
    private int positionHandle;
    private int texturePositionHandle;
    private FloatBuffer textureBuffer;
    private FloatBuffer verticesBuffer;
    private boolean useImageRectF = false;

    private static final float fboRatio = 0.58536583f;

    private void initializeBuffers() {

        if (useImageRectF) {
            vertices[0] = imgShowDomain.left;
            vertices[1] = imgShowDomain.bottom;

            vertices[2] = imgShowDomain.right;
            vertices[3] = imgShowDomain.bottom;

            vertices[4] = imgShowDomain.left;
            vertices[5] = imgShowDomain.top;

            vertices[6] = imgShowDomain.right;
            vertices[7] = imgShowDomain.top;
            verticesBuffer = BufferUtils.getFloatBufferForFLoatArray(vertices);
        } else {
            verticesBuffer = BufferUtils.getFloatBufferForFLoatArray(vertices);
        }
        verticesBuffer.position(0);


//        textureVertices[1] = fboRatio;
//        textureVertices[3] = fboRatio;
//        textureVertices[5] = fboRatio;


        textureBuffer = BufferUtils.getFloatBufferForFLoatArray(textureVertices);
        textureBuffer.position(0);
    }

    private void initializeProgram() {
        program = ShaderUtils.generateShaderProgram(mContext, "drawline_vertex_shader.glsl", "drawline_fragment_shader.glsl");
    }

    public void draw(int texture) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUseProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, CommonParameters.A_POSITION);
        textureHandle = GLES20.glGetUniformLocation(program, CommonParameters.U_SAMPLE);
        texturePositionHandle = GLES20.glGetAttribLocation(program, CommonParameters.A_TEXTURECOORD);

        Log.i(TAG, "positionHandle:" + positionHandle);
        Log.i(TAG, "textureHandle:" + textureHandle);
        Log.i(TAG, "texturePositionHandle:" + texturePositionHandle);

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        int params[] = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, params,0);
        Log.i(TAG,"GL_MAX_VERTEX_ATTRIBS params[0]:"+ params[0]);
        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_UNIFORM_VECTORS, params, 0);
        Log.i(TAG, "GL_MAX_VERTEX_UNIFORM_VECTORS params[0]:" + params[0]);

    }

    public Square(Context context) {
        this.mContext = context;
        initializeBuffers();
        initializeProgram();
    }

    public Square(Context context, Bitmap bitmap, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mContext = context;
        imgShowDomain = ImageUtils.getImageRectF(bitmap, width, height);
        Log.i(TAG, "image width ratio:" + imgShowDomain.right);
        Log.i(TAG, "image height ratio:" + imgShowDomain.top);
        useImageRectF = true;
        initializeBuffers();
        initializeProgram();
    }

    private static final float initialPositiveWidth = 1.0f;
    private static final float initialNegativeWidth = -1.0f;
    private static final float initialPositiveHeight = 1.0f;
    private static final float initialNegativeHeight = -1.0f;

    private float vertices[] = {
            initialNegativeWidth, initialNegativeHeight,
            initialPositiveWidth, initialNegativeHeight,
            initialNegativeWidth, initialPositiveHeight,
            initialPositiveWidth, initialPositiveHeight
    };
    private float textureVertices[] = {
            textureStaNumber, textureStaNumber,//left bottom
            textureEndNumber, textureStaNumber,//right bottom
            textureStaNumber, textureEndNumber,//left top
            textureEndNumber, textureEndNumber//right top
    };

    private static final float textureEndNumber = 1.0f;//0.43902436//0.7083334
    private static final float textureStaNumber = 0.0f;
}
