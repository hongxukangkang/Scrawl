package com.meitu.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.meitu.switchbutton.CheckSwitchButton;


public class SelfLayoutActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private Context context;
    private CheckSwitchButton csv_push;
    private CheckSwitchButton csv_sound;
    private CheckSwitchButton csv_shake;
    private CheckSwitchButton csv_alarm_push;

    private ProgressDialog mPushOpenPrg;
    private ProgressDialog mAlarmOpenPrg;
    private ProgressDialog mPushClosePrg;
    private ProgressDialog mAlarmClosePrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_layout);

        findViews();
        setOnCheckedChangeListeners();
    }

    private void setOnCheckedChangeListeners() {
        csv_push.setOnCheckedChangeListener(this);
    }

    private void findViews() {
        csv_push = (CheckSwitchButton) findViewById(R.id.csv_push);
        csv_sound = (CheckSwitchButton) findViewById(R.id.csv_sound);
        csv_shake = (CheckSwitchButton) findViewById(R.id.csv_shake);
        csv_alarm_push = (CheckSwitchButton) findViewById(R.id.csv_alarm_push);
        context = SelfLayoutActivity.this;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.csv_push:
                if (isChecked) {//开启百度推送服务
                    String apiKey = "";
                    mPushOpenPrg = ProgressDialog.show(context, null, getString(R.string.openning_push_service), true, true);
                    mPushOpenPrg.setCanceledOnTouchOutside(false);
                } else {//关闭百度推送服务
                    mPushClosePrg = ProgressDialog.show(context, null, getString(R.string.closing_push_service), true, true);
                    mPushClosePrg.setCanceledOnTouchOutside(false);
                }

                csv_sound.setEnabled(isChecked);
                csv_shake.setEnabled(isChecked);
                csv_alarm_push.setEnabled(isChecked);
                break;
        }
    }
}