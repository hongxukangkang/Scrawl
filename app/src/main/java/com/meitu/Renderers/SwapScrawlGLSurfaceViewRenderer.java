package com.meitu.Renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.meitu.android.R;
import com.meitu.utils.ShaderUtils;
import com.meitu.widgetObjects.PositionPoint;
import com.meitu.widgetObjects.ScrawlBrush;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mtdiannao on 2015/6/17.
 */
public class SwapScrawlGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "ScrawlGLSurfaceViewRenderer";

    private static final float background_r = 0.8f;
    private static final float background_g = 0.7f;
    private static final float background_b = 0.6f;
    private static final float background_a = 1.0f;
    private static final int BYTES_PER_FLOAT = 4;

    private Bitmap brushBitmap;
    private Bitmap mBitmap;
    private int programId;
    private int[] textureId;
    private Context mContext;
    private ScrawlBrush brush;

    private int []frameBufferS;
    private int []renderBufferS;

    private int bitmapWidth;
    private int bitmapHeight;

    private int mWidth;
    private int mHeight;

    private Queue<PositionPoint> pointQueue = new LinkedList<PositionPoint>();

    public SwapScrawlGLSurfaceViewRenderer(Context context) {
        this.mContext = context;
        textureId = new int[2];
        frameBufferS = new int[1];
        renderBufferS = new int[1];
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initialRunnableContext(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        GLES20.glViewport(0, 0, width, height);
        this.mWidth = width;
        this.mHeight = height;
        initialTexture();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        finishBackGround();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferS[0]);
        brush.onDrawWithBrush(pointQueue);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

//        updataScreenWithFBO();

    }

    private void updataScreenWithFBO() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glUniform1f(textureSamplerHandle, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,0,vertexBuffer);

        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void finishBackGround() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public void initialRunnableContext(Context upRunnableContext) {
        GLES20.glClearColor(background_r, background_g, background_b, background_a);
        initialShaderProgram();

    }

    private void initialTexture() {
        brushBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.brush_round);
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pomelo_test);
        this.bitmapWidth = mBitmap.getWidth();
        this.bitmapHeight = mBitmap.getHeight();

        GLES20.glGenTextures(2, textureId, 0);

//        GLES20.glBindRenderbuffer(GLES20.GL_FRAMEBUFFER,renderBufferS[0]);
//        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT,mWidth,mHeight);
//        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER,renderBufferS[0]);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        GLES20.glGenFramebuffers(1, frameBufferS, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferS[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId[0],0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[1]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, brushBitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferS[1]);
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId[1], 0);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        brush = new ScrawlBrush(programId, textureId[1], mContext);
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.i(TAG, "====glFramebufferRenderbuffer WRONG....");
        } else {
            Log.i(TAG, "====glFramebufferRenderbuffer success....");
        }
    }

    public void setBrushWidth(float brushWidth, float brushHeight){
        brush.setBrushWidth(brushWidth, brushHeight);
    }

    private void initialShaderProgram() {

        programId = ShaderUtils.generateShaderProgram(mContext, "scrawl_vertex_shader.glsl", "scrawl_fragment_shader.glsl");

        positionHandle = GLES20.glGetAttribLocation(programId,A_POSITION);
        GLES20.glEnableVertexAttribArray(positionHandle);
        textureCoordHandle = GLES20.glGetAttribLocation(programId, A_TEXTURECOORD);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexData).position(0);
        textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(textureCoordData).position(0);

        textureSamplerHandle = GLES20.glGetUniformLocation(programId, U_TEXTURE_SAMPLER);

        int buffer[] = new int[1];
        GLES20.glGenBuffers(1,buffer,0);
        textureCoordBufferId = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,textureCoordBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,textureCoordBuffer.capacity() * BYTES_PER_FLOAT,textureCoordBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glGenBuffers(1, buffer, 0);
        vertexBufferId = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void addQueueDab(float rawX,float rawY,boolean newEvent) {
        PositionPoint point = new PositionPoint();
        point.x = rawX;
        point.y = rawY;
        point.isStroken = newEvent;
        pointQueue.offer(point);
    }

    public void clearQueue(){
        pointQueue.clear();
    }

    public ScrawlBrush getBrush(){
        return brush;
    }

    //vertex of rectangle domain
    private float[] vertexData = {
            -1.0f, -0.5f, 0.0f,//left bottom
            1.0f, -0.5f, 0.0f,//right bottom
            -1.0f, 0.5f, 0.0f,//left top
            1.0f, 0.5f, 0.0f//right top
    };

    //texture coordinate
    private float[] textureCoordData = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };
    private static final String A_POSITION = "a_position";
    private static final String U_TEXTURE_SAMPLER = "u_texture";
    private static final String A_TEXTURECOORD = "a_textureCoord";
    private int positionHandle;
    private int textureCoordHandle;
    private int textureSamplerHandle;
    private int vertexBufferId;
    private int textureCoordBufferId;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;
}