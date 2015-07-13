package com.meitu.Renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.meitu.android.R;
import com.meitu.utils.CommonParameters;
import com.meitu.utils.ImageUtils;
import com.meitu.utils.ShaderUtils;
import com.meitu.widgetObjects.BrushDab;
import com.meitu.widgetObjects.PathBrush;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.graphics.BitmapFactory.decodeResource;

/**
 * Created by mtdiannao on 2015/6/24.
 */
public class PathGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "PathGLSurfaceViewRenderer";

    private static final float BACKGROUND_R = 0.5f;
    private static final float BACKGROUND_G = 0.8f;
    private static final float BACKGROUND_B = 0.5f;
    private static final float BACKGROUND_A = 1.0f;
    private static final int BYTES_PER_FLOAT = 4;
    private boolean isCanSaveBitmap = false;

    private int screenWidth;//
    private int screenHeight;//

    private int mProgramId;//
    private Context mContext;

    private int[] textureId;//
    private int[] frameBuffer;//
    private int[] renderBuffer;//

    private PathBrush brush;//
    private Queue<BrushDab> brushDabs = new LinkedList<BrushDab>();

    private float[] vertex = {
            -1.0f, -0.5f,//left bottom;
            1.0f, -0.5f,//right bottom;
            -1.0f, 0.5f,//left top
            1.0f, 0.5f//right top
    };

    private float[] textureCord = {
            0.0f, 1.0f,//left top
            1.0f, 1.0f,//right top
            0.0f, 0.0f,//left bottom
            1.0f, 0.0f //right bottom
    };

    private Bitmap mBitmap;//
    private int uSamplerHandle;//
    private int textureCordHandle;//
    private int vertexPositionHandle;//
    private FloatBuffer vertextBuffer;//
    private FloatBuffer textureCordBuffer;//

    private int fbo_width;
    private int fbo_height;
    private RectF imgShowDomain;//

    public PathGLSurfaceViewRenderer(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initialRunnableContext(mContext);
    }

    /***/
    private void initialRunnableContext(Context context) {
        try {

            mBitmap = decodeResource(context.getResources(), R.drawable.bg_width_height1);//pomelo_test
            fbo_width = mBitmap.getWidth();
            fbo_height = mBitmap.getHeight();

            GLES20.glClearColor(BACKGROUND_R, BACKGROUND_G, BACKGROUND_B, BACKGROUND_A);
            mProgramId = ShaderUtils.generateShaderProgram(mContext, "path_vertex_shader.glsl", "path_fragment_shader.glsl");
            textureId = new int[2];
            GLES20.glGenTextures(2, textureId, 0);
            brush = new PathBrush(context, mProgramId, textureId[0]);

            //
            GLES20.glUseProgram(mProgramId);
            uSamplerHandle = GLES20.glGetUniformLocation(mProgramId, CommonParameters.U_SAMPLE);
            textureCordHandle = GLES20.glGetAttribLocation(mProgramId, CommonParameters.A_TEXTURECOORD);
            vertexPositionHandle = GLES20.glGetAttribLocation(mProgramId, CommonParameters.A_POSITION);

            //
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[1]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            GLES20.glUniform1f(uSamplerHandle, 0);//


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****/
    private void generateBufferForData() {

        vertex[0] = imgShowDomain.left;
        vertex[1] = imgShowDomain.bottom;

        vertex[2] = imgShowDomain.right;
        vertex[3] = imgShowDomain.bottom;

        vertex[4] = imgShowDomain.left;
        vertex[5] = imgShowDomain.top;

        vertex[6] = imgShowDomain.right;
        vertex[7] = imgShowDomain.top;

        vertextBuffer = ByteBuffer.allocateDirect(vertex.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertextBuffer.put(vertex).position(0);

        textureCordBuffer = ByteBuffer.allocateDirect(textureCord.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCordBuffer.put(textureCord).position(0);
    }

    /****/
    private void generateFrameBuffer() {
        frameBuffer = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId[1], 0);

        renderBuffer = new int[1];
        GLES20.glGenRenderbuffers(1, renderBuffer, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT, fbo_width, fbo_height);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_RENDERBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        Log.i(TAG, "==status:==" + status);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        this.screenWidth = width;
        this.screenHeight = height;
        setImageDomain();
        generateFrameBuffer();
        generateBufferForData();
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        if (!isCanSaveBitmap && brushDabs.size() > 0) {
            isCanSaveBitmap = true;
        }
        if (brushDabs.size() > 0) {
            brush.draw(brushDabs);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        updateScreenWidthFbo();
    }

    /**
     *
     */
    private void updateScreenWidthFbo() {

        GLES20.glUseProgram(mProgramId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[1]);

        GLES20.glEnableVertexAttribArray(vertexPositionHandle);
        GLES20.glVertexAttribPointer(vertexPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vertextBuffer);

        GLES20.glEnableVertexAttribArray(textureCordHandle);
        GLES20.glVertexAttribPointer(textureCordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }

    public PathBrush getBrush() {
        return brush;
    }

    public void addDab(float rawX, float rawY, boolean flag) {
        float deviceCordX = rawX * 2 / screenWidth - 1.0f;
        float deviceCordY = (rawY * 2 / screenHeight - 1.0f);
        Log.i("PathBrushPosition", "touch position (tx,ty):(" + deviceCordX + "," + deviceCordY + ")");
        BrushDab brushDab = new BrushDab(deviceCordX, deviceCordY, flag);
        brushDabs.offer(brushDab);
    }


    /**
     *
     */
    private void setImageDomain() {
        imgShowDomain = ImageUtils.getImageRectF(mBitmap, screenWidth, screenHeight);
    }

    private int mSpaceHeight;

    public void setSpaceHeight(int spaceHeight) {
        this.mSpaceHeight = spaceHeight;
    }
}