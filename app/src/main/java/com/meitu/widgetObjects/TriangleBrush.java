package com.meitu.widgetObjects;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.meitu.utils.BufferUtils;
import com.meitu.utils.CommonParameters;
import com.meitu.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Queue;

/**
 * Created by mtdiannao on 2015/6/30.
 */
public class TriangleBrush {

    private static final String TAG = "TriangleBrush";
    private static final int BYTES_PER_FLOAT = 4;
    private static final float BRUSHWIDTH = 0.025F;
    private static final float BRUSHHEIGHT = 0.015F;

    private int colorHandle;
    private int positionHanle;

    private FloatBuffer vertexBuffer;
    private static float ratio = 1.0f;//0.58536583f

    private float lastX;
    private float lastY;

    private static final float initialNegativeTopX = -0.05f;
    private static final float initialNegativeTopY = 0.05f;
    private static final float initialNegativeBotY = -0.05f;

    private float[] brushVertex = {//x,y
            initialNegativeTopX / ratio, initialNegativeTopY * ratio,
            initialNegativeBotY / ratio, initialNegativeBotY * ratio,
            initialNegativeTopY / ratio, initialNegativeTopX * ratio
//            0.05f, -0.05f
    };

    private int mProgramId;
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";

    public TriangleBrush(Context context, int textureId) {

        mProgramId = ShaderUtils.generateShaderProgram(context, "brush_vertex_shader.glsl", "brush_fragment_shader.glsl");

//        brushVertex[0] = 2 * initialNegativeTopX;
//        brushVertex[0] = 2 * initialNegativeBotY;
//        brushVertex[0] = 2 * initialNegativeTopY;

        vertexBuffer = BufferUtils.getFloatBufferForFLoatArray(brushVertex);
        vertexBuffer.position(0);

        GLES20.glUseProgram(mProgramId);

        colorHandle = GLES20.glGetUniformLocation(mProgramId, U_COLOR);
        positionHanle = GLES20.glGetAttribLocation(mProgramId, A_POSITION);

    }

    private float brushColorR = 1.0f;
    private float brushColorG = 0.3f;
    private float brushColorB = 0.3f;

    public void draw() {

        GLES20.glVertexAttribPointer(positionHanle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHanle);

        GLES20.glUniform4f(colorHandle, brushColorR, brushColorG, brushColorB, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public void draw(Queue<BrushDab> dabs){
        GLES20.glUseProgram(mProgramId);
        colorHandle = GLES20.glGetUniformLocation(mProgramId, U_COLOR);
        positionHanle = GLES20.glGetAttribLocation(mProgramId, A_POSITION);

        GLES20.glUniform4f(colorHandle, brushColorR, brushColorG, brushColorB, 1.0f);
        GLES20.glEnableVertexAttribArray(colorHandle);

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

                brushVertex[0] = x - BRUSHWIDTH;//
                brushVertex[1] = y - BRUSHHEIGHT;//

                brushVertex[2] = x + BRUSHWIDTH;//
                brushVertex[3] = y - BRUSHHEIGHT;//

                brushVertex[4] = x - BRUSHWIDTH;//
                brushVertex[5] = y + BRUSHHEIGHT;//

                vertexBuffer = ByteBuffer.allocateDirect(brushVertex.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
                vertexBuffer.put(brushVertex).position(0);

                GLES20.glEnableVertexAttribArray(positionHanle);
                GLES20.glVertexAttribPointer(positionHanle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

            }
            lastX = brushDab.x_coordinate;
            lastY = brushDab.y_coordinate;
        }
    }

    public float[] getVertexArray() {
        return brushVertex;
    }

    public void setBrushColor(float brushColorR, float brushColorG, float brushColorB) {
        this.brushColorB = brushColorB;
        this.brushColorG = brushColorG;
        this.brushColorR = brushColorR;
    }
}