package com.example.jamiehong.jumpgame;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Spike {
    // used for drawing
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
    
    // for draw method
    private int mPositionHandle;
    private int mColorHandle;

    public float[] mModelMatrix = new float[16];

    private int mMVPMatrixHandle;
    
    // phone dimensions (landscape)
    public float offset = -2.5f; //portrait mode: -1.5f, landscape: -2.5f
    private float base = 0.125f;
    private float height = 0.125f;

    private long timeStart;
    private double constant;
    
    // player's jumping and landing velocity
    public static final float velocity = 0.01f;

    private int COORDS_PER_VERTEX = 3;
    private float[] triangleCoords = {
            offset + 0.0f,  height, 0.0f, // top
            offset + base, -height, 0.0f, // bottom left
            offset - base, -height, 0.0f // bottom right
    };

    // sets color of spike (white)
    private float[] color = {1.0f, 1.0f, 1.0f, 1.0f}; // white

    private int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Spike(long timeStart, double constant) {
        this.timeStart = timeStart;
        this.constant = constant;

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

    public void changeStartTime(long timeStart) {
        this.timeStart = timeStart;
    }

    public void moveSpike(long currentTime) {
        // the larger constant is, the "collision" will happen later (more to left)
        // the smaller constant is, the "collision" will happen earlier (more to right)
        long timeElapsed = currentTime - timeStart;
        
        // calculate the distance the spike has traveled
        float distTraveled = (float)(velocity * timeElapsed / constant);

        // update distTraveled
        for(int i = 0; i < movingCoords.length; i++) {
            movingCoords[i] = triangleCoords[i];
            if(i % 3 == 0) {
                movingCoords[i] += distTraveled;
            }
        }
    }

    // getter method for spike coordinates
    public float[] getSpikeCoords() {
        return movingCoords;
    }

    // Collision detection:
    // returns true if spike and player's X and Y coordinates overlap
    // false otherwise
    public boolean collide(Player p) {
        
        // booleans for checking and keeping track
        boolean sameX = false;
        boolean sameY = false;
        
        // get player's coordinates
        float[] playerCoords = p.getPlayerCoords();
        float PlayerLXPos = playerCoords[3];
        float PlayerRXPos = playerCoords[6];

        // check X coordinates
        if(((movingCoords[3] >= PlayerLXPos) && (movingCoords[3] <= PlayerRXPos))
                || ((movingCoords[6] >= PlayerLXPos) && (movingCoords[6] <= PlayerRXPos))) {
            sameX = true;
        }

        // check Y coordinates
        if(movingCoords[1] >= playerCoords[4]) {
            sameY = true;
        }
        
        return sameX && sameY;
    }
}
