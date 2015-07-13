package com.meitu.widgets;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.meitu.Renderers.SwapScrawlGLSurfaceViewRenderer;

/**
 * Created by mtdiannao on 2015/6/17.
 */
public class SwapScrawlGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "ScrawlGLSurfaceView";
    private static final int GLSURFACEVEIW_VERSION = 2;

    private Context mContext;
    private SwapScrawlGLSurfaceViewRenderer mRender;

    public SwapScrawlGLSurfaceView(Context context) {
        super(context);
        initialRunnableContext(context);
    }

    private void initialRunnableContext(Context context) {
        mContext = context;
        setEGLContextClientVersion(GLSURFACEVEIW_VERSION);
        mRender = new SwapScrawlGLSurfaceViewRenderer(mContext);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public SwapScrawlGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialRunnableContext(context);
    }

    private boolean newEvent;
    private static final float BRUSH_WIDTH = 0.035f;
    private static final float BRUSH_HEIGHT = 0.024f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        newEvent = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                newEvent = true;
            case MotionEvent.ACTION_MOVE:
//                Toast.makeText(mContext,"Click",Toast.LENGTH_SHORT).show();
                final boolean isNewEvent = newEvent;
                final int i = event.getActionIndex();
                final float x = event.getX(i);
                final float y = event.getY(i);
                final float p = event.getPressure(i);
                final float rawX = event.getX();
                final float rawY = event.getRawY() ;//+touchHeight
                queueEvent(new Runnable() {
                    public void run() {
                        mRender.setBrushWidth(BRUSH_WIDTH, BRUSH_HEIGHT);
//                        mRender.setTouchVertexBuffer(rawX, rawY);
                        mRender.addQueueDab(rawX, rawY, isNewEvent);
                        requestRender();
                        Log.i(TAG, "=====HELLO=======");
                    }
                });
                return true;
        }
        return false;
    }

    private float touchHeight;
    public void setHeight(float height){
        this.touchHeight = height;
    }

    public SwapScrawlGLSurfaceViewRenderer getMRender(){
        return mRender;
    }
}
