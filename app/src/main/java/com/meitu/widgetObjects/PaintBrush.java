package com.meitu.widgetObjects;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.util.Log;

import com.meitu.utils.BufferUtils;
import com.meitu.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mtdiannao on 2015/6/30.
 */
public class PaintBrush {

    private static final String TAG = "PaintBrush";

    private int colorHandle;
    private int positionHandle;

    private FloatBuffer brushVertexBuffer;
    private FloatBuffer brushColorBuffer;

    private Context mContext;
    private RectF brushPosition;//

    private int mProgramId;

    public PaintBrush(Context context) {
        this.mContext = context;
        this.mProgramId = ShaderUtils.generateShaderProgram(mContext, "brush_paint_vertex_shader.glsl", "brush_paint_fragment_shader.glsl");

        brushColorBuffer = ByteBuffer.allocateDirect(brushColor.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        brushColorBuffer.position(0);

        brushVertexBuffer = ByteBuffer.allocateDirect(brushVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        brushVertexBuffer.position(0);

        GLES20.glUseProgram(mProgramId);
        colorHandle = GLES20.glGetAttribLocation(mProgramId, A_COLOR);
        positionHandle = GLES20.glGetAttribLocation(mProgramId, A_POSITION);
        Log.i(TAG, "mProgramId:" + mProgramId);
        Log.i(TAG, "colorHandle:" + colorHandle + "-->positionHandle:" + positionHandle);
    }

    public void draw() {///RectF position


//        brushVertex[0] = position.left;

        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, brushVertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, brushColorBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

//        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void setBrushPostionDomain(RectF position) {
        this.brushPosition = position;

    }

    public void setBrushColor(float colorRValue, float colorGValue, float colorBValue) {
        //left bottom
        brushColor[0] = colorRValue;
        brushColor[1] = colorGValue;
        brushColor[2] = colorBValue;
        //left top
        brushColor[4] = colorRValue;
        brushColor[5] = colorGValue;
        brushColor[6] = colorBValue;
        //right bottom
        brushColor[8] = colorRValue;
        brushColor[9] = colorGValue;
        brushColor[10] = colorBValue;
        //left bottom
        brushColor[12] = colorRValue;
        brushColor[13] = colorGValue;
        brushColor[14] = colorBValue;

        brushColorBuffer = BufferUtils.getFloatBufferForFLoatArray(brushColor);
        brushColorBuffer.position(0);

    }

    private float[] brushVertex = {//xyz
//            0.0f, 0.5f, 0.0f,//left bottom
//            -0.5f, -0.5f, 0.0f,//left top
//            0.5f, -0.5f, 0.0f,//right bottom
//            0.5f, 0.5f, 0.0f,//right top
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f
    };

    private float[] brushColor = {//RGBA
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
//            1.0f, 0.0f, 0.0f, 1.0f
    };

    private static final String A_COLOR = "aColor";
    private static final String A_POSITION = "aPosition";

}
