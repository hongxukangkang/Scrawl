package com.meitu.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mtdiannao on 2015/6/30.
 */
public class BufferUtils {

    public static FloatBuffer getFloatBufferForFLoatArray(float[] array){
        FloatBuffer result = null;
        ByteBuffer buffer = ByteBuffer.allocateDirect(array.length * CommonParameters.BYTES_PER_FLOAT);
        buffer = buffer.order(ByteOrder.nativeOrder());
        result = buffer.asFloatBuffer().put(array);
        return result;
    }
}
