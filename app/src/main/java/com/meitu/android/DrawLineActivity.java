package com.meitu.android;

import android.app.Activity;
import android.os.Bundle;

import com.meitu.widgets.DrawLineGLSurfaceView;


public class DrawLineActivity extends Activity {

    private DrawLineGLSurfaceView mSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_draw_line);

//        mSurfaceView = (DrawLineGLSurfaceView) findViewById(R.id.id_drawLineGLSurfaceView);
        mSurfaceView = new DrawLineGLSurfaceView(this);
        setContentView(mSurfaceView);

    }
}
