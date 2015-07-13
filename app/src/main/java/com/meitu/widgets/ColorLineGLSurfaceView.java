package com.meitu.widgets;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.meitu.Renderers.ColorGLSurfaceViewRenderer;
import com.meitu.widgetObjects.TriangleBrush;

/**
 * Created by mtdiannao on 2015/7/1.
 */
public class ColorLineGLSurfaceView extends GLSurfaceView {

    ColorGLSurfaceViewRenderer mRender;
    public ColorLineGLSurfaceView(Context context) {
        super(context);
        intialRunnableContext(context);
    }

    private void intialRunnableContext(Context context) {
        setEGLContextClientVersion(2);
        mRender = new ColorGLSurfaceViewRenderer(context);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public ColorLineGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        intialRunnableContext(context);
    }

    public ColorGLSurfaceViewRenderer getmRender(){
        return mRender;
    }


    private boolean isStroken = false;
    private boolean clickSpaceFlag = true;
    private TriangleBrush mBrush;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isStroken = false;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                clickSpaceFlag = false;
                mBrush = mRender.getBrush();
            case MotionEvent.ACTION_POINTER_DOWN:
                isStroken = true;
            case MotionEvent.ACTION_MOVE:

                final boolean isNewStroken = isStroken;
                final float downX = event.getRawX();
                final float downY = event.getRawY();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mRender.addDab(downX, downY, isNewStroken);
                        requestRender();
                    }
                });
            case MotionEvent.ACTION_UP:
                return true;
        }
        return false;
    }
}