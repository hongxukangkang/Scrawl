package com.meitu.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.meitu.utils.CommonParameters;
import com.meitu.widgets.PathGLSurfaceView;


public class PathActivity extends Activity {

    private PathGLSurfaceView mSurfaceView;
//    private LinearLayout bottomContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_path);

        findViewsById();
//        setListenersForWidgets();

    }

    private void findViewsById() {
        uiLayoutListener = new PhoneUILayoutListener();
        mSurfaceView = (PathGLSurfaceView) findViewById(R.id.id_path_glsurfaceview);
//        bottomContainer = (LinearLayout) findViewById(R.id.id_bottomLayout);
    }

    private void setListenersForWidgets() {
        ViewTreeObserver vto = mSurfaceView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(uiLayoutListener);
    }


    private boolean isCalculated = false;
    private PhoneUILayoutListener uiLayoutListener;

    private final class PhoneUILayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            if (!isCalculated) {
                isCalculated = true;
//                int height = bottomContainer.getLayoutParams().height;
//                mSurfaceView.setSpaceHeight(height);

                int surfaceViewHeight = mSurfaceView.getHeight();
                int surfaceViewWidth = mSurfaceView.getWidth();
                Log.i(CommonParameters.PATH_ACTIVITY_TAG, "===surfaceViewWidth:" + surfaceViewWidth);
                Log.i(CommonParameters.PATH_ACTIVITY_TAG, "***surfaceViewHeight:" + surfaceViewHeight);
            }
        }
    }
}
