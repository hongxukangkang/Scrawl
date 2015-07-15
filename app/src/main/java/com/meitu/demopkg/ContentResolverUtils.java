package com.meitu.demopkg;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by mtdiannao on 2015/7/15.
 */
public class ContentResolverUtils {

    private static final String TAG = ContentResolverUtils.class.getSimpleName();

    public static void testContentResolver(Context context) {
        ContentResolver cr = context.getContentResolver();
        String[] path = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED, "MAX(" + MediaStore.Images.Media.DATE_MODIFIED + ")"};
        for (int i = 0; i < path.length; i++) {
            Log.i(TAG, "=======++++++++" + path[i]);
        }
    }
}
