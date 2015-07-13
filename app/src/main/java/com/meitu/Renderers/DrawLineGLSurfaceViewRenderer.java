package com.meitu.Renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.meitu.android.R;
import com.meitu.utils.ImageUtils;
import com.meitu.widgetObjects.TriangleBrush;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 *
 * Created by mtdiannao on 2015/6/27.
 */
public class DrawLineGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "DrawLineGLSurfaceViewRenderer";

    private static final float COLOR_R = 1.0f;
    private static final float COLOR_G = 1.0f;
    private static final float COLOR_B = 0.5f;
    private static final float COLOR_A = 1.0f;

    private int mViewWidth;
    private int mViewHeight;
    private Context mContext;
    private TriangleBrush brush;

    private Bitmap mBitmap;//
    private RectF imageDisplayDomain;//
    private FloatBuffer vertexDistrictBuffer;//
    private FloatBuffer textureCoordinateBuffer;//

    private float[] imageDomainCord;//
    private float[] imageDomainTextureCord;//

    //fbo
    private int mProgramId;
    private int[] fboHandle;
    private int samplerHandle;
    private int positionHandle;
    private int textureCordHandle;

    private int[] textureId;

    public DrawLineGLSurfaceViewRenderer(Context context) {
        this.mContext = context;
        fboHandle = new int[1];
        textureId = new int[1];
        initialRunnableContext(mContext);
    }

    private void initialRunnableContext(Context context) {
        imageDisplayDomain = new RectF();
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pomelo_test);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(COLOR_R, COLOR_G, COLOR_B, COLOR_A);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        mViewWidth = width;
        mViewHeight = height;
        GLES20.glViewport(0, 0, width, height);
        imageDisplayDomain = ImageUtils.getImageRectF(mBitmap, mViewWidth, mViewHeight);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    private float[] vertexDistrict = {
            -1.0f, -0.5f,//left bottom;
            1.0f, -0.5f,//right bottom;
            -1.0f, 0.5f,//left top
            1.0f, 0.5f//right top
    };

    private float[] textureCoordinate = {
            0.0f, 1.0f,//left top
            1.0f, 1.0f,//right top
            0.0f, 0.0f,//left bottom
            1.0f, 0.0f //right bottom
    };
}