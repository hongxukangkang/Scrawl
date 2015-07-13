package com.meitu.Renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.meitu.android.R;
import com.meitu.utils.RGBAGLSurfaceViewParameters;
import com.meitu.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mtdiannao on 2015/7/7.
 */
public class AnotherRGBAGLSurfaceViewRender implements GLSurfaceView.Renderer {

    private static final String TAG = "AnotherRGBAGLSurfaceViewRender";

    private Context mContext;
    private int[] textureId;
    private int mProgramId;
    private int positionHandle;
    private int textureCordHandle;
    private int textureSamplerHandle;

    //ÏñËØµÄÑÕÉ«Öµ
    private final float CLEAR_COLOR_R = 0.5f;
    private final float CLEAR_COLOR_G = 0.5f;
    private final float CLEAR_COLOR_B = 0.5f;
    private final float CLEAR_COLOR_A = 1.0f;

    private final float negativeOne = -1.0f;
    private final float positiveOne = 1.0f;
    private final float negativeFive = -0.5f;
    private final float positiveFive = 0.5f;

    //the district of texture
    private float textureDomain[] = {
            negativeOne, negativeFive,//left bottom
            positiveOne, negativeFive,//right bottom
            negativeOne, positiveFive,//left top
            positiveOne, positiveFive//right top
    };

    //the district of texture coordinate
    private float textureCord[] = {
            0.0f, 1.0f,// left top
            1.0f, 1.0f,// right top
            0.0f, 0.0f,// left bottom
            1.0f, 0.0f // right bottom
    };

    private int textureCordBufferId;
    private int textureDomainBufferId;

    private Bitmap textureBitmap;//the background bitmap of texture

    private int textureWidth;
    private int textureHeight;
    private IntBuffer pixelsBuffer;

    public AnotherRGBAGLSurfaceViewRender(Context context) {
        this.mContext = context;
        textureId = new int[1];
        textureBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_width_height1);
        textureWidth = textureBitmap.getWidth();
        textureHeight = textureBitmap.getHeight();
        Log.i(TAG, "===textureWidth:" + textureWidth);
        Log.i(TAG, "===textureHeight:" + textureHeight);
    }

    private void generateBufferId() {

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(textureDomain.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(textureDomain).position(0);
        int positionBuffer[] = new int[1];
        GLES20.glGenBuffers(1, positionBuffer, 0);
        textureDomainBufferId = positionBuffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureDomainBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        FloatBuffer textureCordBuffer = ByteBuffer.allocateDirect(textureCord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCordBuffer.put(textureCord).position(0);
        Log.i(TAG, "textureCordBuffer.capacity()" + textureCordBuffer.capacity());
        int cordBuffer[] = new int[1];
        GLES20.glGenBuffers(1, cordBuffer, 0);
        textureCordBufferId = cordBuffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCordBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureCordBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private int generateTexture(int width, int height) {

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId[0];

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(CLEAR_COLOR_R, CLEAR_COLOR_G, CLEAR_COLOR_B, CLEAR_COLOR_A);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        int hei = textureBitmap.getHeight();
        int wid = textureBitmap.getWidth();
        GLES20.glViewport(0, 0, width, height);
        generateBufferId();
        generateTexture(wid, hei);
        mProgramId = ShaderUtils.generateShaderProgram(mContext, "rgb_vertex_shader.glsl", "rgb_fragment_shader.glsl");
        if (mProgramId != 0) {
            positionHandle = GLES20.glGetAttribLocation(mProgramId, RGBAGLSurfaceViewParameters.POSITION);
            textureCordHandle = GLES20.glGetAttribLocation(mProgramId, RGBAGLSurfaceViewParameters.TEXCOORD);
            textureSamplerHandle = GLES20.glGetUniformLocation(mProgramId, RGBAGLSurfaceViewParameters.SAMPLER);
            Log.i(TAG, "===positionHandle:" + positionHandle);
            Log.i(TAG, "===textureCordHandle:" + textureCordHandle);
            Log.i(TAG, "===textureSamplerHandle:" + textureSamplerHandle);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgramId);
        //draw texture

        GLES20.glEnableVertexAttribArray(positionHandle);
//        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, textureDomainBuffer);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCordBufferId);
        GLES20.glEnableVertexAttribArray(textureCordHandle);
        GLES20.glVertexAttribPointer(textureCordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureDomainBufferId);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glUniform1f(textureSamplerHandle, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //finish
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}