package com.meitu.widgetObjects;

import android.content.Context;
import android.opengl.GLES20;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Queue;

/**
 * Created by mtdiannao on 2015/6/17.
 */
public class ScrawlBrush {

    private Queue<Object> objectQueue;
    private int programId;
    private int textureId;

    private static final int BRUSH_COLOR = 0x00000fff;
    private float brushWidth = 0.025f;
    private float brushHeight = 0.015f;
    private static final int BYTE_PER_FLOAT = 4;
    private static final int DAB_STEPS = 45;

    private static final String A_POSITION = "a_position";
    private static final String TEXTURE_SAMPLE = "u_texture";
    private static final String A_TEXTURECOORD = "a_textureCoord";

    private Context mContex;
    private int positionHandle;
    private int textureCoordHandle;
    private int textureSampleHandle;

    private int screenWidth;
    private int screenHeight;
    private float deviceCoordX;
    private float deviceCoordY;

    private int vertexBufferId;
    private int textureCoordBufferId;

    public ScrawlBrush(int programId, int textureId, Context mContex) {
        this.mContex = mContex;
        this.textureId = textureId;
        this.programId = programId;

        DisplayMetrics dm = mContex.getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;

        initialVertexBuffer();
        initialTextureCoordBuffer();


    }

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;

    private final float[] vertexData = {
            -0.85f, -0.85f, 0.0f,//left bottom
            -0.8f, -0.85f, 0.0f,//right bottom
            -0.85f, -0.8f, 0.0f,//left top
            -0.8f, -0.8f, 0.0f,//right top
    };

    /* Coordinates for texture mapping the brush texture to the polygon */
    private final float[] textureCoordData = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    private void initialVertexBuffer() {
        int buffer[] = new int[1];
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTE_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexData).position(0);
        GLES20.glGenBuffers(1, buffer,0);
        vertexBufferId = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vertexBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vertexBuffer.capacity() * BYTE_PER_FLOAT,vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void initialTextureCoordBuffer() {
        int buffer[] = new int[1];
        textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordData.length * BYTE_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(textureCoordData).position(0);
        GLES20.glGenBuffers(1, buffer, 0);
        textureCoordBufferId = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCoordBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureCoordBuffer.capacity() * BYTE_PER_FLOAT, textureCoordBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void setBrushWidth(float brushWidth, float brushHeight) {
        this.brushWidth = brushWidth;
        this.brushHeight = brushHeight;

    }

    private float rawX;
    private float rawY;

    public void setVertexBuffer(float rawX, float rawY) {

        this.rawX = rawX;
        this.rawY = rawY;

        deviceCoordX = rawX * 2 / screenWidth - 1.0f;
        deviceCoordY = -(rawY * 2 / screenHeight - 1.0f);

        //left bottom
        vertexData[0] = deviceCoordX - brushWidth;
        vertexData[1] = deviceCoordY - brushHeight;
        vertexData[2] = 0.0f;

        //right bottom
        vertexData[3] = deviceCoordX + brushWidth;
        vertexData[4] = deviceCoordY - brushHeight;
        vertexData[5] = 0.0f;

        //left top
        vertexData[6] = deviceCoordX - brushWidth;
        vertexData[7] = deviceCoordY + brushHeight;
        vertexData[8] = 0.0f;

        //right top
        vertexData[9] = deviceCoordX + brushWidth;
        vertexData[10] = deviceCoordY + brushHeight;
        vertexData[11] = 0.0f;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTE_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexData).position(0);
    }


    public void onDrawWithBrush(Queue<PositionPoint> points) {

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        GLES20.glUseProgram(programId);
        GLES20.glDisable(GLES20.GL_BLEND);

        textureSampleHandle = GLES20.glGetUniformLocation(programId, TEXTURE_SAMPLE);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1f(textureSampleHandle,0);

        positionHandle = GLES20.glGetAttribLocation(programId, A_POSITION);

        textureCoordHandle = GLES20.glGetAttribLocation(programId, A_TEXTURECOORD);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        while (!points.isEmpty()) {
            PositionPoint point = points.poll();
            setVertexBuffer(point.x, point.y);
            float x, y;
            if (point.isStroken){
                x = point.x;
                y = point.y;
            }else{
                x = lastX;
                y = lastY;
            }
            int i = DAB_STEPS;
            while (i-- != 0) {

                if (point.isStroken) {
                    break;
                } else {
                    /* calculate the next brush mark position to paint */
                    float dx = (point.x - lastX);
                    float dy = (point.y - lastY);
                    x += dx / DAB_STEPS;
                    y += dy / DAB_STEPS;
                }

//                vertexData[0] = x - brushWidth;
//                vertexData[1] = y - brushHeight;
//
//                vertexData[3] = x + brushWidth;
//                vertexData[4] = y - brushHeight;
//
//                vertexData[6] = x - brushWidth;
//                vertexData[7] = y + brushHeight;
//
//                vertexData[9] = x + brushWidth;
//                vertexData[10] = y + brushHeight;

                vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTE_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
                vertexBuffer.put(vertexData).position(0);

                GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                GLES20.glEnableVertexAttribArray(positionHandle);

                GLES20.glUniform1f(textureSampleHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            }
            lastX = point.x;
            lastY = point.y;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private float lastX;
    private float lastY;

}