package com.example.jamiehong.jumpgame;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Spike {
    private FloatBuffer vertexBuffer;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;

    public float[] mModelMatrix = new float[16];

    private int mMVPMatrixHandle;

    public float offset = -2.5f; //portrait mode: -1.5f, landscape: -2.5f
    private float base = 0.125f;
    private float height = 0.125f;

    private long timeOffset;

    private int COORDS_PER_VERTEX = 3;
    private float[] triangleCoords = {
            offset + 0.0f,  height, 0.0f, // top
            offset + base, -height, 0.0f, // bottom left
            offset - base, -height, 0.0f // bottom right
    };

    private float[] color = {1.0f, 1.0f, 1.0f, 1.0f}; // white

    private int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Spike(long timeOffset) {
        this.timeOffset = timeOffset;

        Matrix.setIdentityM(mModelMatrix, 0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private float[] movingCoords = new float[triangleCoords.length];

    public void moveSpike(long startTime, long currentTime, float velocity) {
        long timeElapsed = currentTime - (startTime + timeOffset);
        int constant = 18;
        // the larger constant is, the "collision" will happen later (more to left)
        // the smaller constant is, the "collision" will happen earlier (more to right)
        float distTraveled = velocity * timeElapsed / constant;


        for(int i = 0; i < movingCoords.length; i++) {
            movingCoords[i] = triangleCoords[i];
            if(i % 3 == 0) {
                movingCoords[i] += distTraveled;
            }
        }
    }

    public float[] getSpikeCoords() {
        return movingCoords;
    }

    public boolean collide(Player p) {
        boolean sameX = false;
        boolean sameY = false;
        float[] playerCoords = p.getPlayerCoords();
        float PlayerLXPos = playerCoords[3];
        float PlayerRXPos = playerCoords[6];

        /*
        for(int i = 3; i < movingCoords.length; i+= 3) {
            float SpikeXPos = movingCoords[i];
            if((SpikeXPos > PlayerLXPos) && (SpikeXPos < PlayerRXPos)) {
                sameX = true;
            }
        }
        */

        if(((movingCoords[3] >= PlayerLXPos) && (movingCoords[3] <= PlayerRXPos))
                || ((movingCoords[6] >= PlayerLXPos) && (movingCoords[6] <= PlayerRXPos))) {
            sameX = true;
        }

        if(movingCoords[1] >= playerCoords[4]) {
            sameY = true;
        }
        //return sameY;
        //return sameX;
        return sameX && sameY;
        //return true;

        /*
        if(movingCoords[0] <= 0) {
            return true;
        } else {
            return false;
        }
        */
    }
}