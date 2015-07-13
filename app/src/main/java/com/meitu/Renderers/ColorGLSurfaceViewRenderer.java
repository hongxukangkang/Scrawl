package com.meitu.Renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.meitu.android.R;
import com.meitu.utils.ImageUtils;
import com.meitu.widgetObjects.BrushDab;
import com.meitu.widgetObjects.Square;
import com.meitu.widgetObjects.TriangleBrush;

import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mtdiannao on 2015/7/1.
 */
public class ColorGLSurfaceViewRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "ColorGLSurfaceViewRenderer";

    private static final float COLOR_R = 0.5f;
    private static final float COLOR_G = 0.5f;
    private static final float COLOR_B = 0.5f;
    private static final float COLOR_A = 1.0f;

    private Bitmap photo;
    private Context mContext;
    private int textures[] = new int[2];

    private int mWidth;
    private int mHeight;

    private int texWidth = 1024;//
    private int texHeight = 1024;//
    private Square square;
    private TriangleBrush brush;

    private int[] fboHandle;
    private int[] renderHandle;

    private Queue<BrushDab> brushDabs = new LinkedList<BrushDab>();

    public ColorGLSurfaceViewRenderer(Context context) {
        super();
        this.mContext = context;
        photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_width_height1);//pomelo_test
        photo = ImageUtils.revertImage(photo);
    }

    private void generateSquare() {
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0, GLES20.GL_RGB, photo.getWidth(), photo.getHeight(),0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE,null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);//GL_LINEAR_MIPMAP_LINEAR
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);//_LINEAR_MIPMAP
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, 1024, 1024, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);
        square = new Square(mContext, photo, mWidth, mHeight);
//        square = new Square(mContext);
    }

    private void generateTriangleBrush() {
        brush = new TriangleBrush(mContext, textures[1]);
    }

    private void generateFboBuffer() {

        IntBuffer maxRenderbufferSize = IntBuffer.allocate(1);
        GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, maxRenderbufferSize);
        Log.i(TAG, "==maxRenderbufferSize.get(0):" + maxRenderbufferSize.get(0));
        while ((maxRenderbufferSize.get(0) <= texWidth) || (maxRenderbufferSize.get(0) <= texHeight)) {
            texWidth = texWidth / 2;
            texHeight = texHeight / 2;
        }
//        generaterRenderBufferObject();

        fboHandle = new int[1];
        GLES20.glGenFramebuffers(1, fboHandle, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboHandle[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
//        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderHandle[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.i(TAG, "use fbo success...");
        } else {
            Log.i(TAG, "use fbo failure...");
        }
    }

    private void generaterRenderBufferObject() {
        renderHandle = new int[1];
        GLES20.glGenRenderbuffers(1, renderHandle, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderHandle[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT, texWidth, texHeight);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    }

    public void addDab(float rawX, float rawY, boolean flag) {
        float deviceCordX = rawX * 2 / mWidth - 1.0f;
        float deviceCordY = -(rawY * 2 / mHeight - 1.0f);
        Log.i("PathBrushPosition", "touch position (tx,ty):(" + deviceCordX + "," + deviceCordY + ")");
        BrushDab brushDab = new BrushDab(deviceCordX, deviceCordY, flag);
        brushDabs.offer(brushDab);
    }

    public TriangleBrush getBrush() {
        return brush;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(COLOR_R, COLOR_G, COLOR_B, COLOR_A);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.mWidth = width;
        this.mHeight = height;
        generateSquare();
        generateTriangleBrush();
        generateFboBuffer();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboHandle[0]);
        if (brushDabs.size() > 0) {
            brush.draw(brushDabs);
        }
        float position[] = brush.getVertexArray();
        for (int i = 0; i < position.length; i++) {
            Log.i(TAG, "position[" + i + "]:" + position[i]);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        square.draw(textures[0]);
    }
}