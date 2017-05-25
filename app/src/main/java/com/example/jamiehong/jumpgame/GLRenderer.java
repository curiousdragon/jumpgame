package com.example.jamiehong.jumpgame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

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
    public static final float PLAYER_VELOCITY = 0.02f;
    public volatile long mStartTime;
    public volatile boolean duringTap;

    private long mStartSpikeTime;

    public void setStartTime(long startTapTime) {
        this.mStartTime = startTapTime;
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
        mStartTime = 0;
        duringTap = false;
        mStartSpikeTime = System.currentTimeMillis();
    }

    public void onPause() {
        //pause renderer
    }

    public void onResume() {
        //resume renderer
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a player
        mPlayer = new Player();

        // initialize a spike
        mSpike = new Spike(System.currentTimeMillis());

        // initialize the ground
        mGround = new Ground();

        // initialize a spike generator
        mSpikeGen = new SpikeGenerator(System.currentTimeMillis());

        mVelocity = 0f;

        mStartSpikeTime = System.currentTimeMillis();

        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Get the current time
        long now = System.currentTimeMillis();
        //long now = SystemClock.elapsedRealtime();
        //long now = SystemClock.uptimeMillis();

        // We should make sure we are valid and sane
        if (mStartTime > now) return;

        // Get the amount of time the last frame took.
        long elapsed = now - mStartTime;

        float[] scratch_player = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // If the user has tapped the screen while player on ground
        // ensures that player will not keep jumping up when multiple taps
        // during a single jump
        if(duringTap) {
            // player reaches top of jump, start moving down
            if(elapsed >= 500 && mVelocity == PLAYER_VELOCITY) {
                mVelocity = -PLAYER_VELOCITY;
            } else if(elapsed >= 1000 && mVelocity == -PLAYER_VELOCITY) {
                // player reaches bottom of jump, stop moving
                mVelocity = 0f;
                // player's tap no longer in effect, can tap again
                duringTap = false;
                // reset player's position on ground
                Matrix.setIdentityM(mPlayer.mModelMatrix, 0);
                // alternate way to reset:
                //mPlayer = new Player();
            }
        }

        Matrix.translateM(mPlayer.mModelMatrix, 0, 0f, mVelocity, 0f);

        //Matrix.translateM(the model matrix,
        // int offset (keep @ 0)
        // x coordinate (- goes to right, + goes to left),
        // y coordinate (- goes to down, + goes to up),
        // z coordinate( how size changes) (+ gets smaller (further away))

        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_player, 0, mMVPMatrix, 0, mPlayer.mModelMatrix, 0);
        // draw the player
        mPlayer.draw(scratch_player);


/*
        mSpikeGen.draw(mMVPMatrix);

        long mTimeElapsed = now - mSpikeGen.mPrevSpike;
        float[] scratch_spike = new float[16];

        mSpikeGen.add();

        for (int i = 0; i < mSpikeGen.list.size(); i ++) {
            scratch_spike = new float[16];
            Spike s = mSpikeGen.list.get(i);
            // Calculate the movement of the spike
            Matrix.translateM(s.mModelMatrix, 0, s.velocity, 0f, 0f);
            // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
            Matrix.multiplyMM(scratch_spike, 0, mMVPMatrix, 0, s.mModelMatrix, 0);
            s.draw(scratch_spike);
            s.moveSpike(now);
        }

        mSpikeGen.remove();
*/

        /*
        boolean bool = false;

        mSpikeGen.list.add(new Spike(now));

        mStartSpikeTime = now - mStartSpikeTime;

        if(mStartSpikeTime > 2000 &&  mStartSpikeTime < 2500) {
            mSpikeGen.list.add(new Spike(now));
            bool = true;
            Spike s = mSpikeGen.list.get(0);

            //Matrix.setIdentityM(s.mModelMatrix, 0);


            if(s.getSpikeCoords()[6] > 1.0) {
                mSpikeGen.list.remove(0);
                s = mSpikeGen.list.get(0);
                Matrix.setIdentityM(s.mModelMatrix, 0);
            }
            mSpikeGen.remove();
        } else if(mStartSpikeTime > 2500) {
            mStartSpikeTime = now;
            bool = false;
        }

        float[] scratch_spike = null;

        for(int i = 0; i < mSpikeGen.list.size(); i++) {
            scratch_spike = new float[16];
            Spike s = mSpikeGen.list.get(i);
            Matrix.translateM(s.mModelMatrix, 0, 0.01f, 0f, 0f);
            // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
            Matrix.multiplyMM(scratch_spike, 0, mMVPMatrix, 0, s.mModelMatrix, 0);
            s.draw(scratch_spike);
            s.moveSpike(now);
        }

        */


        float[] scratch_spike = new float[16];

        // Calculate the movement of the spike
        Matrix.translateM(mSpike.mModelMatrix, 0, 0.01f, 0f, 0f);

        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_spike, 0, mMVPMatrix, 0, mSpike.mModelMatrix, 0);
        // draw the spike
        mSpike.draw(scratch_spike);

        mSpike.moveSpike(now);

        mPlayer.movePlayer(elapsed, PLAYER_VELOCITY, duringTap);

        boolean bool = false;

        //testing to see if moveSpike and movePlayer and collide work
        if(mSpike.collide(mPlayer) && !bool) {
            // do something
            bool = true;
            //mGround.draw(mMVPMatrix);
        }

        if(bool) {
            mGround.draw(mMVPMatrix);
        }

        // draw the ground
        //mGround.draw(mMVPMatrix);
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

