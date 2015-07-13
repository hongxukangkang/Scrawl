package com.meitu.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by mtdiannao on 2015/7/1.
 */
public class CoordinateUtils {

    /**
     * *
     */
    public float[] generateTexureCoordinate(Context context, float rawX, float rawY) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float[] deviceCoord = new float[2];
        float x = rawX / screenWidth;
        float y = -(rawY / screenHeight) + 1.0f;
        deviceCoord[0] = x;
        deviceCoord[1] = y;
        return deviceCoord;
    }

}
