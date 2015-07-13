package com.meitu.widgets;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.meitu.Renderers.DrawLineGLSurfaceViewRenderer;

/**
 * Created by mtdiannao on 2015/6/27.
 */
public class DrawLineGLSurfaceView extends GLSurfaceView {

    private static  final int GLSURFACEVIEW_VERSION = 2;

    private DrawLineGLSurfaceViewRenderer mRender;

    private Context mContext;

    public DrawLineGLSurfaceView(Context context) {
        super(context);
        initialRunnableContext(context);
    }
    public DrawLineGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialRunnableContext(context);
    }

    private void initialRunnableContext(Context context) {
        this.mContext = context;
        setEGLContextClientVersion(GLSURFACEVIEW_VERSION);
        mRender = new DrawLineGLSurfaceViewRenderer(context);
        setRenderer(mRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
