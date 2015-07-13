package com.meitu.widgets;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.meitu.Renderers.PathGLSurfaceViewRenderer;
import com.meitu.widgetObjects.PathBrush;

/**
 * Created by mtdiannao on 2015/6/24.
 */
public class PathGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "PathGLSurfaceView";

    private static final int GLSURFACEVIEW_VERSION = 2;
    private PathGLSurfaceViewRenderer mRenderer;
//    private Context mContext;

    private int screenWidth;
    private int screenHeight;

    public PathGLSurfaceView(Context context) {
        super(context);
        initialRunnableContext(context);
    }
    public PathGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialRunnableContext(context);
    }

    private void initialRunnableContext(Context context){

        mRenderer = new PathGLSurfaceViewRenderer(context);
        setEGLContextClientVersion(GLSURFACEVIEW_VERSION);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setFocusable(true);
        setFocusableInTouchMode(true);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }

    private boolean isStroken = false;
    private PathBrush mBrush;
    private boolean clickSpaceFlag = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isStroken = false;
        int action = event.getAction();

        float down_Y = event.getY();

//        if(down_Y < screenHeight - mSpaceHeight){
//            clickSpaceFlag = false;
//            return true;
//        }

        switch(action){
            case MotionEvent.ACTION_DOWN:
                clickSpaceFlag = false;
                mBrush = mRenderer.getBrush();
            case MotionEvent.ACTION_POINTER_DOWN:
                isStroken = true;
            case MotionEvent.ACTION_MOVE:

                final boolean isNewStroken = isStroken;
                Log.i(TAG,"isNewStroken-->"+ isNewStroken);
                final float downX = event.getRawX();
                final float downY = event.getRawY();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
//                        if (!clickSpaceFlag){
                            mRenderer.addDab(downX, downY, isNewStroken);
                            requestRender();
                        }
//                    }
                });
            case MotionEvent.ACTION_UP:
                return true;
        }
        return false;
    }

    private int mSpaceHeight;
    public void setSpaceHeight(int spaceHeight){
        this.mSpaceHeight = spaceHeight;
        mRenderer.setSpaceHeight(spaceHeight);
    }
}
