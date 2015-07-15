package com.meitu.android;

import android.app.Activity;
import android.os.Bundle;

import com.meitu.demopkg.ContentResolverUtils;


public class ContenteResolverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contente_resolver);

        testContentResolver();
    }

    private void testContentResolver() {
        ContentResolverUtils.testContentResolver(this);
    }
}
