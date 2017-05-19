/*
package com.example.jamiehong.jumpgame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.support.v4.view.GravityCompat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Player mPlayer;
    private Spike mSpike;
    private Ground mGround;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    //private float[] mRotationMatrix = new float[16];

    public volatile float mVelocity;
    private long mLastTime;
    public volatile long mStartTime;
    public volatile boolean duringTap;

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public void setDuringTap(boolean duringTap) {
        this.duringTap = duringTap;
    }

    public boolean isDuringTap() {
        return duringTap;
    }

    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }

    public GLRenderer() {
        mLastTime = System.currentTimeMillis() + 100;
        mStartTime = 0;
        duringTap = false;
    }

    public void onPause() {
        //pause renderer
    }

    public void onResume() {
        //resume renderer
        mLastTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a player
        mPlayer = new Player();

        // initialize a spike
        mSpike = new Spike();

        // initialize the ground
        mGround = new Ground();

        mVelocity = 0f;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Get the current time
        long now = System.currentTimeMillis();

        // We should make sure we are valid and sane
        if (mStartTime > now) return;

        // Get the amount of time the last frame took.
        long elapsed = now - mStartTime;

        float[] scratch_player = new float[16];
        float[] scratch_spike = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        if(duringTap) {
            if(elapsed >= 500 && mVelocity != -0.02f) {
                mVelocity = -0.02f;
            } else if(elapsed >= 1000 && mVelocity == -0.02f) {
                mVelocity = 0f;
                duringTap = false;
                Matrix.setIdentityM(mPlayer.mModelMatrix, 0);
                //mPlayer = new Player();
            }
        }
        Matrix.translateM(mPlayer.mModelMatrix, 0, 0f, mVelocity, 0f);


        //Matrix.translateM(mPlayer.mModelMatrix, 0, -0.02f, 0f, 0f);
        //Matrix.translateM(the model matrix,
        // int offset (keep @ 0)
        // x coordinate (- goes to right, + goes to left),
        // y coordinate (- goes to down, + goes to up),
        // z coordinate( how size changes) (+ gets smaller (further away))

        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_player, 0, mMVPMatrix, 0, mPlayer.mModelMatrix, 0);
        // draw the player
        mPlayer.draw(scratch_player);

        // Calculate the movement of the spike
        Matrix.translateM(mSpike.mModelMatrix, 0, 0.01f, 0f, 0f);

        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_spike, 0, mMVPMatrix, 0, mSpike.mModelMatrix, 0);
        // draw the spike
        mSpike.draw(scratch_spike);

        // draw the ground
        mGround.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
*/

package com.example.jamiehong.jumpgame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.support.v4.view.GravityCompat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Player mPlayer;
    private Spike mSpike;
    private SpikeGenerator mSpikeGen;
    private Ground mGround;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    //private float[] mRotationMatrix = new float[16];

    public volatile float mVelocity;
    private long mLastTime;
    public volatile long mStartTime;
    public volatile boolean duringTap;

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public void setDuringTap(boolean duringTap) {
        this.duringTap = duringTap;
    }

    public boolean isDuringTap() {
        return duringTap;
    }

    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }

    public GLRenderer() {
        mLastTime = System.currentTimeMillis() + 100;
        mStartTime = 0;
        duringTap = false;
    }

    public void onPause() {
        //pause renderer
    }

    public void onResume() {
        //resume renderer
        mLastTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a player
        mPlayer = new Player();

        // initialize a spike
        mSpike = new Spike();

        // initialize a spike generator
        mSpikeGen = new SpikeGenerator();

        // initialize the ground
        mGround = new Ground();

        mVelocity = 0f;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Get the current time
        long now = System.currentTimeMillis();

        // We should make sure we are valid and sane
        if (mStartTime > now) return;

        // Get the amount of time the last frame took.
        long elapsed = now - mStartTime;

        float[] scratch_player = new float[16];
        float[] scratch_spike = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        if(duringTap) {
            if(elapsed >= 500 && mVelocity != -0.02f) {
                mVelocity = -0.02f;
            } else if(elapsed >= 1000 && mVelocity == -0.02f) {
                mVelocity = 0f;
                duringTap = false;
                Matrix.setIdentityM(mPlayer.mModelMatrix, 0);
                //mPlayer = new Player();
            }
        }
        Matrix.translateM(mPlayer.mModelMatrix, 0, 0f, mVelocity, 0f);


        //Matrix.translateM(mPlayer.mModelMatrix, 0, -0.02f, 0f, 0f);
        //Matrix.translateM(the model matrix,
        // int offset (keep @ 0)
        // x coordinate (- goes to right, + goes to left),
        // y coordinate (- goes to down, + goes to up),
        // z coordinate( how size changes) (+ gets smaller (further away))

        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_player, 0, mMVPMatrix, 0, mPlayer.mModelMatrix, 0);
        // draw the player
        mPlayer.draw(scratch_player);

        // Calculate the movement of the spikes
        for (Spike spike : SpikeGenerator.list) {
            Matrix.translateM(spike.mModelMatrix, 0, 0.01f, 0f, 0f);


            // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
            Matrix.multiplyMM(scratch_spike, 0, mMVPMatrix, 0, spike.mModelMatrix, 0);


            // draw the spike
            spike.draw(scratch_spike);
        }


        // draw the ground
        mGround.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}

