package com.meitu.widgetObjects;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mtdiannao on 2015/6/17.
 */
public class RectangleShape {

    private static final String TAG = "RectangleShape";
    private static final int BYTES_PER_FLOAT = 4;
    private static final String A_POSITION = "a_position";
    private static final String U_TEXTURE_SAMPLE = "u_texture";
    private static final String A_TEXTURECOORD = "a_textureCoord";
    private int textureId;
    private int textureBufferId;//0
    private int positionBufferId;//

    public RectangleShape(int programId,int textureId) {
        this.programId = programId;
        this.textureId = textureId;

        int buffer[] = new int[1];
        vertexBuffer = ByteBuffer.allocateDirect(vertexDomin.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexDomin).position(0);
        GLES20.glGenBuffers(1, buffer, 0);
        positionBufferId = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        textureCoordBuffer = ByteBuffer.allocateDirect(vetexTextureCoord.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(vetexTextureCoord).position(0);
        GLES20.glGenBuffers(1, buffer, 0);
        textureBufferId = buffer[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureCoordBuffer.capacity() * BYTES_PER_FLOAT, textureCoordBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        initialAllHandles();
    }

    private void initialAllHandles() {
        positionHandle = GLES20.glGetAttribLocation(programId, A_POSITION);
        textureCoordHandle = GLES20.glGetAttribLocation(programId, A_TEXTURECOORD);
        textureSampleHandle = GLES20.glGetUniformLocation(programId, U_TEXTURE_SAMPLE);
    }

    private int programId;

    private int positionHandle;
    private int textureCoordHandle;
    private int textureSampleHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;

    //vertex of rectangle domain
    private float[] vertexDomin = {
            -1.0f, -0.5f, 0.0f,//left bottom
             1.0f, -0.5f, 0.0f,//right bottom
            -1.0f,  0.5f, 0.0f,//left top
             1.0f,  0.5f, 0.0f//right top
    };

    //texture coordinate
    private float[] vetexTextureCoord = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    public void drawRectangle() {

//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(programId);
        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1f(textureSampleHandle, 0);

        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}