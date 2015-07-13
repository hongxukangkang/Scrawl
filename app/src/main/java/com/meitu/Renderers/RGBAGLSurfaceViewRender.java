package com.meitu.Renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;

import com.meitu.android.R;
import com.meitu.utils.ImageUtils;
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
public class RGBAGLSurfaceViewRender implements GLSurfaceView.Renderer {

    private static final String TAG = "RGBAGLSurfaceViewRender";

    private Context mContext;
    private int[] textureId;
    private int mProgramId;
    private int positionHandle;
    private int textureCordHandle;
    private int textureSamplerHandle;

    private int viewPortWidth;
    private int viewPortHeight;

    private final float CLEAR_COLOR_R = 0.5f;
    private final float CLEAR_COLOR_G = 0.5f;
    private final float CLEAR_COLOR_B = 0.5f;
    private final float CLEAR_COLOR_A = 1.0f;

    //ÏñËØµÄÑÕÉ«Öµ
    private int pixels[] = {
            255, 0, 0, // Red
            255, 0, 0, // Green
            255, 0, 0, // Blue
            255, 0, 0 // Yellow
    };

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
            1.0f, 0.0f// right bottom
    };

    private FloatBuffer textureCordBuffer;//the buffer of texture coordinate
    private FloatBuffer textureDomainBuffer;//the buffer of texture district
    private Bitmap textureBitmap;//the background bitmap of texture

    private int textureWidth;
    private int textureHeight;
    private IntBuffer pixelsBuffer;

    private int fboBuffer[];

    public RGBAGLSurfaceViewRender(Context context) {
        this.mContext = context;
        textureId = new int[1];
        textureBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_width_height1);
        textureWidth = textureBitmap.getWidth();
        textureHeight = textureBitmap.getHeight();

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        viewPortWidth = dm.widthPixels;
        viewPortHeight = dm.heightPixels;

        RectF mRectF = ImageUtils.getImageRectF(textureBitmap, viewPortWidth, viewPortHeight);

        pixelsBuffer = ByteBuffer.allocateDirect(pixels.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        pixelsBuffer.put(pixels).position(0);

        textureCordBuffer = ByteBuffer.allocateDirect(textureCord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCordBuffer.put(textureCord).position(0);

        textureDomain[0] = mRectF.left;
        textureDomain[1] = mRectF.bottom;

        textureDomain[2] = mRectF.right;
        textureDomain[3] = mRectF.bottom;

        textureDomain[4] = mRectF.left;
        textureDomain[5] = mRectF.top;

        textureDomain[6] = mRectF.right;
        textureDomain[7] = mRectF.top;

        textureDomainBuffer = ByteBuffer.allocateDirect(textureDomain.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureDomainBuffer.put(textureDomain).position(0);

    }

    private int generateTexture(int width, int height) {

        int[] pixels = new int[textureWidth * textureHeight];
        textureBitmap.getPixels(pixels, 0, textureWidth, 0, 0, textureWidth, textureHeight);
        ByteBuffer bb = ByteBuffer.allocateDirect(textureWidth * textureHeight * 4);
        IntBuffer ib = bb.asIntBuffer();
        ib.put(pixels).position(0);//

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, textureWidth, textureHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId[0];

    }

    private void generateFrameRenderBuffer() {

        int renderBuffer[] = new int[1];
        GLES20.glGenRenderbuffers(1, renderBuffer, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT, textureWidth, textureHeight);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        fboBuffer = new int[1];
        GLES20.glGenFramebuffers(1, fboBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId[0], 0);

        int renderSize[] = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, renderSize, 0);
        if (renderSize[0] <= textureHeight) {
            textureHeight = renderSize[0];
        }
        if (renderSize[0] <= textureWidth) {
            textureWidth = renderSize[0];
        }

        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(CLEAR_COLOR_R, CLEAR_COLOR_G, CLEAR_COLOR_B, CLEAR_COLOR_A);
        int hei = textureBitmap.getHeight();
        int wid = textureBitmap.getWidth();
        GLES20.glViewport(0, 0, viewPortWidth, viewPortHeight);
        generateTexture(wid, hei);
        mProgramId = ShaderUtils.generateShaderProgram(mContext, "rgb_vertex_shader.glsl", "rgb_fragment_shader.glsl");
        if (mProgramId != 0) {
            positionHandle = GLES20.glGetAttribLocation(mProgramId, RGBAGLSurfaceViewParameters.POSITION);
            textureCordHandle = GLES20.glGetAttribLocation(mProgramId, RGBAGLSurfaceViewParameters.TEXCOORD);
            textureSamplerHandle = GLES20.glGetUniformLocation(mProgramId, RGBAGLSurfaceViewParameters.SAMPLER);
        }

        generateFrameRenderBuffer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);
        GLES20.glUseProgram(mProgramId);
        //draw texture

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, textureDomainBuffer);

        GLES20.glEnableVertexAttribArray(textureCordHandle);
        GLES20.glVertexAttribPointer(textureCordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCordBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glUniform1f(textureSamplerHandle, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //finish
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}