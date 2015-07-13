package com.meitu.widgets;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.meitu.Renderers.RGBAGLSurfaceViewRender;
import com.meitu.utils.RGBAGLSurfaceViewParameters;

/**
 * Created by mtdiannao on 2015/7/7.
 */
public class RGBAGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    RGBAGLSurfaceViewRender mRender;
//    private AnotherRGBAGLSurfaceViewRender mRender;//RGBAGLSurfaceViewRender

    public RGBAGLSurfaceView(Context context) {
        super(context);
        initialRunnableContext(context);
    }

    private void initialRunnableContext(Context context) {
        this.mContext = context;
        setEGLContextClientVersion(RGBAGLSurfaceViewParameters.GLSURFACEVIEW_VERSION);
//        mRender = new AnotherRGBAGLSurfaceViewRender(mContext);
        mRender = new RGBAGLSurfaceViewRender(mContext);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public RGBAGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
