package com.meitu.widgetObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.meitu.android.R;
import com.meitu.utils.CommonParameters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Queue;

/**
 * Created by mtdiannao on 2015/6/24.
 */
public class PathBrush {//GLES20

    private static final String TAG = "PathBrushPosition";
    private static final int BYTES_PER_FLOAT = 4;
    private static final float BRUSHWIDTH = 0.025F;
    private static final float BRUSHHEIGHT = 0.015F;
    private Bitmap brushBitmap;

    private float[] vertex = {
            -0.025f, -0.015f,//left bottom;
            0.025f, -0.015f,//right bottom;
            -0.025f, 0.015f,//left top
            0.025f, 0.015f//right top

//            - 1.0f, -0.5f,//left bottom;
//            1.0f, -0.5f,//right bottom;
//            -1.0f, 0.5f,//left top
//            1.0f, 0.5f//right top
    };

    private float[] textureCord = {
            0.0f, 0.0f,//left bottom
            1.0f, 0.0f,//right bottom
            0.0f, 1.0f,//left top
            1.0f, 1.0f//right top
    };

    private int uSampleHandle;
    private int positionHandle;
    private int textureCordHandle;

    private int mProgramId;
    private int mTextureId;

    private float lastX;
    private float lastY;

    private int vetexBuffer;
    private int textureCordBuffer;

    private FloatBuffer vertexFloatBuffer;
    private FloatBuffer textureCordFloatBuffer;

    public PathBrush(Context context, int programId, int textureId) {//GLES20
        this.mProgramId = programId;
        this.mTextureId = textureId;

        brushBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dm_10000_1);//brush_round

        uSampleHandle = GLES20.glGetUniformLocation(mProgramId, CommonParameters.U_SAMPLE);
        positionHandle = GLES20.glGetAttribLocation(mProgramId, CommonParameters.A_POSITION);
        textureCordHandle = GLES20.glGetAttribLocation(mProgramId, CommonParameters.A_TEXTURECOORD);

        vertexFloatBuffer = ByteBuffer.allocateDirect(vertex.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexFloatBuffer.put(vertex).position(0);

        textureCordFloatBuffer = ByteBuffer.allocateDirect(textureCord.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCordFloatBuffer.put(textureCord).position(0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, brushBitmap, 0);
        GLES20.glUniform1i(uSampleHandle, 0);

//        generateDataBuffer();

    }

    private void generateDataBuffer() {

        int buffer[] = new int[1];
        GLES20.glGenBuffers(1, buffer, 0);
        vetexBuffer = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vetexBuffer);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexFloatBuffer.capacity() * BYTES_PER_FLOAT, vertexFloatBuffer, GLES20.GL_STREAM_DRAW);//GL_STATIC_DRAW
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glGenBuffers(1, buffer, 0);
        textureCordBuffer = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCordBuffer);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureCordFloatBuffer.capacity() * BYTES_PER_FLOAT, textureCordFloatBuffer, GLES20.GL_STREAM_DRAW);//GL_STATIC_DRAW
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }

    public void draw(Queue<BrushDab> dabs) {
//        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glUseProgram(mProgramId);
        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        //
        textureCord[0] = 0.68f;
        textureCord[2] = 0.68f;
        textureCord[4] = 0.68f;

        textureCord[1] = 0.68f;
        textureCord[3] = 0.68f;
        textureCord[5] = 0.68f;

        textureCordFloatBuffer = ByteBuffer.allocateDirect(textureCord.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCordFloatBuffer.put(textureCord).position(0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCordBuffer);
        GLES20.glEnableVertexAttribArray(textureCordHandle);
        GLES20.glVertexAttribPointer(textureCordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCordFloatBuffer);

        while (!dabs.isEmpty()) {
            BrushDab brushDab = dabs.poll();
            float x, y;
            if (brushDab.isNewStroken) {
                x = brushDab.x_coordinate;
                y = brushDab.y_coordinate;
            } else {
                x = lastX;
                y = lastY;
            }
            Log.i(TAG, "brush position:(x,y):(" + x + "," + y + ")");
            int i = CommonParameters.DAB_STEP;
            while ((i--) != 0) {
                if (brushDab.isNewStroken) {
                    break;
                } else {
                    /** calculate the next brush mark position to paint */
                    float dx = (brushDab.x_coordinate - lastX);
                    float dy = (brushDab.y_coordinate - lastY);
                    x += dx / CommonParameters.DAB_STEP;
                    y += dy / CommonParameters.DAB_STEP;
                }
                vertex[0] = x - BRUSHWIDTH;//
                vertex[1] = y - BRUSHHEIGHT;//

                vertex[2] = x + BRUSHWIDTH;//
                vertex[3] = y - BRUSHHEIGHT;//

                vertex[4] = x - BRUSHWIDTH;//
                vertex[5] = y + BRUSHHEIGHT;//

                vertex[6] = x + BRUSHWIDTH;//
                vertex[7] = y + BRUSHHEIGHT;//

                vertexFloatBuffer = ByteBuffer.allocateDirect(vertex.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
                vertexFloatBuffer.put(vertex).position(0);

                GLES20.glEnableVertexAttribArray(positionHandle);
                GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexFloatBuffer);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            }
            lastX = brushDab.x_coordinate;
            lastY = brushDab.y_coordinate;
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}