package com.meitu.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {

    private Context ctx;
    private Button btn_dot;
    private Button btn_rgb;
    private Button btn_color;
    private Button btn_scrawl;
    private Button btn_drawLine;
    private Button btn_selfLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setListenersForBtn();
    }

    private void findViews() {
        ctx = this;
        btn_rgb = (Button) findViewById(R.id.id_btn_rgb);
        btn_dot = (Button) findViewById(R.id.id_btn_dot);
        btn_color = (Button) findViewById(R.id.id_btn_color);
        btn_scrawl = (Button) findViewById(R.id.id_btn_scrawl);
        btn_drawLine = (Button) findViewById(R.id.id_btn_line);
        btn_selfLayout = (Button) findViewById(R.id.button3);
    }

    private void setListenersForBtn() {
        btn_rgb.setOnClickListener(this);
        btn_dot.setOnClickListener(this);
        btn_color.setOnClickListener(this);
        btn_scrawl.setOnClickListener(this);
        btn_drawLine.setOnClickListener(this);
        btn_selfLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.id_btn_dot:
                intent.setClass(ctx, PathActivity.class);
                break;
            case R.id.id_btn_scrawl:
                intent.setClass(ctx, ScrawlActivity.class);
                break;
            case R.id.id_btn_line:
                intent.setClass(ctx, DrawLineActivity.class);
                break;
            case R.id.id_btn_color:
                intent.setClass(ctx, ColorLineActivity.class);
                break;
            case R.id.button3:
                intent.setClass(ctx, SelfLayoutActivity.class);
                break;
            case R.id.id_btn_rgb:
                intent.setClass(ctx, RGBATextureActivity.class);
                break;
        }
        startActivity(intent);
    }
}