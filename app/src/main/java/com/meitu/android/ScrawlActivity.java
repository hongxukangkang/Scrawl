package com.meitu.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.meitu.widgets.SwapScrawlGLSurfaceView;


public class ScrawlActivity extends Activity implements View.OnClickListener{//

    private SwapScrawlGLSurfaceView mSurfaceView;
    private PhoneUIGlobalLayoutListener uiLayoutListener;

    private ImageButton ibv_clear;
//    private LinearLayout headerLayout;
    private LinearLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mSurfaceView = new SwapScrawlGLSurfaceView(this);
        setContentView(R.layout.activity_scrawl);
        mSurfaceView = (SwapScrawlGLSurfaceView)findViewById(R.id.id_scrawl_glSurfaceView);
        uiLayoutListener = new PhoneUIGlobalLayoutListener();
        mSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(uiLayoutListener);

//        headerLayout = (LinearLayout)findViewById(R.id.id_header_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.id_bottom_layout);

        ibv_clear = (ImageButton)findViewById(R.id.id_img_clear);
        ibv_clear.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrawl, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.id_brush_large:
                break;
            case R.id.id_brush_small:
                break;
            case R.id.id_brush_medium:
                break;
        }
        return true;
    }

    private boolean hasLayout = false;
    private static final String TAG = "ScrawlActivity";

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.id_img_clear:
                mSurfaceView.getMRender().clearQueue();
                mSurfaceView.requestRender();
                break;
        }

    }

    private final class PhoneUIGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            if (!hasLayout){
                hasLayout = true;
//                int headerHeight = headerLayout.getHeight();
                int bottomHeight = bottomLayout.getHeight();
//                Log.i(TAG,"headerHeight:"+ headerHeight+"-->bottomHeight:"+ bottomHeight);
//                mSurfaceView.setHeight((headerHeight));
            }
        }
    }
}