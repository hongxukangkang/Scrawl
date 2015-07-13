package com.meitu.android;

import android.app.Activity;
import android.os.Bundle;

import com.meitu.widgets.RGBAGLSurfaceView;


public class RGBATextureActivity extends Activity {

    private RGBAGLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new RGBAGLSurfaceView(this);
//        setContentView(R.layout.activity_rgbatexture);
        setContentView(mSurfaceView);
    }
}