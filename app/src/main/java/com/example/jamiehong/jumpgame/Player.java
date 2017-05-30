package com.example.jamiehong.jumpgame;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Player {
    // the player is represented by a green square

    // used for drawing the square
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    // used for the square's vertices
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // used for shading
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    // used for drawing
    private final int mProgram;

    // for draw method
    private int mPositionHandle;
    private int mColorHandle;

    // for any translations of square
    public float[] mModelMatrix = new float[16];

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    // used for drawing coordinates
    private float offset = 0.25f;
    // size of the square
    private float size = 0.125f;

    // number of coordinates per vertex in this array
    private int COORDS_PER_VERTEX = 3;
    // initial drawing coords of square
    private float squareCoords[] = {
            -size + offset,  size, 0.0f,  // top left
            -size + offset, -size, 0.0f,  // bottom left
            size + offset, -size, 0.0f,   // bottom right
            size + offset,  size, 0.0f    // top right
    };


    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue, and alpha (opacity) values
    // color is green
    private float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    public Player() {
        Matrix.setIdentityM(mModelMatrix, 0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

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

        // Enable a handle to the square vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the square coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the square
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    // used to store player's coordinates
    private float[] movingCoords = new float[squareCoords.length];

    // calculating player movements, putting it in movingCoords
    public void movePlayer(long timeElapsed, float velocity, boolean hasTapped) {
        float distTraveled = 0f;

        // if the user has not tapped, then the square has not moved
        // if the user HAS tapped, then the square has moved
        if(hasTapped) {
            // calculate the distance traveled
            distTraveled = velocity * timeElapsed;
        }

        // put the new coordinates into movingCoords
        for(int i = 0; i < movingCoords.length; i++) {
            movingCoords[i] = squareCoords[i];

            // if it is movingCoords[1], [4], [7], [10]
            // then add the distTraveled
            // (since it means it is the y-coord of one of the vertices)
            if(i % 3 == 1) {
                movingCoords[i] += distTraveled;
            }
        }
    }

    // getter method for getting the player's coordinates
    public float[] getPlayerCoords() {
        return movingCoords;
    }
}
