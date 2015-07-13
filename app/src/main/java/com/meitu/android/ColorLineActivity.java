package com.meitu.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.meitu.widgets.ColorLineGLSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ColorLineActivity extends Activity implements View.OnClickListener {

    private ImageButton btn_save;
    private ColorLineGLSurfaceView mSurafceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mSurafceView = new ColorLineGLSurfaceView(this);
//        setContentView(mSurafceView);
        setContentView(R.layout.activity_color_line);

//        findViews();
//        setListeners();

    }

    private void setListeners() {
        btn_save.setOnClickListener(this);
    }

    private void findViews() {
        btn_save = (ImageButton) findViewById(R.id.ibv_save);
        mSurafceView = (ColorLineGLSurfaceView) findViewById(R.id.clv_scrawl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibv_save:
//                Bitmap bitmap = mSurafceView.getmRender().getBitmapFromGLSurfaceView();
//                saveBitmap(bitmap);
                break;
        }
    }

    private void saveBitmap(Bitmap bitmap) {
        if (bitmap == null){
            return;
        }
        try {
            String saveDir = "data/data/scrawl";
            String fileName = String.valueOf(System.currentTimeMillis());
            File dir = new File(saveDir + File.separator);
            dir.mkdirs();
            File outFile = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
