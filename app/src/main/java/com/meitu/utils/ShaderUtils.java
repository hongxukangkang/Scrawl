package com.meitu.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by mtdiannao on 2015/6/17.
 */
public class ShaderUtils {

    private static final String TAG = "ShaderUtils";
    private static final String NEW_LINE_SEPERATOR = "\n";

    private static String getShaderScriptS(Context context, String shaderSrc) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream in = context.getAssets().open(shaderSrc);
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String temp = reader.readLine();
        while (temp != null) {
            stringBuffer.append(temp).append(NEW_LINE_SEPERATOR);
            temp = reader.readLine();
        }
        return stringBuffer.toString();
    }

    public static int generateShaderProgram(Context context, String vertexSrc, String fragmentSrc) {
        int programId = 0;
        try {
            String vertexShader = getShaderScriptS(context, vertexSrc);
            String fragmentShader = getShaderScriptS(context, fragmentSrc);

            Log.i(TAG, "vertexShader:" + vertexShader);
            Log.i(TAG, "fragmentShader:" + fragmentShader);
            int vertexShaderId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            int fragmentShaderId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            if (vertexShaderId == 0 || fragmentShaderId == 0) {
                return 0;
            }
            int params[] = new int[1];

            GLES20.glShaderSource(vertexShaderId, vertexShader);
            GLES20.glShaderSource(fragmentShaderId, fragmentShader);
            GLES20.glCompileShader(vertexShaderId);
            GLES20.glCompileShader(fragmentShaderId);

            GLES20.glGetShaderiv(vertexShaderId, GLES20.GL_COMPILE_STATUS, params, 0);

            if (params[0] == 0) {
                Log.v(TAG, "Results of compiling source:" + "\n" + "\n:" + GLES20.glGetShaderInfoLog(vertexShaderId));
                return 0;
            }
            GLES20.glGetShaderiv(fragmentShaderId, GLES20.GL_COMPILE_STATUS, params, 0);

            if (params[0] == 0) {
                Log.v(TAG, "Results of compiling source:" + "\n" + "\n:" + GLES20.glGetShaderInfoLog(fragmentShaderId));
                return 0;
            }
            int linkParams[] = new int[1];
            programId = GLES20.glCreateProgram();
            GLES20.glAttachShader(programId, vertexShaderId);
            GLES20.glAttachShader(programId, fragmentShaderId);
            GLES20.glLinkProgram(programId);
            GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkParams, 0);
            if (linkParams[0] == 0) {
                Log.v(TAG, "Results of compiling source:" + "\n" + "\n:" + GLES20.glGetProgramInfoLog(programId));
                return 0;
            }
            Log.v(TAG, "shader success ");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "IO Exception ");
        }
        return programId;
    }
}
