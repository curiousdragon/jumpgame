package com.example.jamiehong.jumpgame;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private Player mPlayer;
    private Spike mSpike;
    private Spike mSpike2;
    private Ground mGround;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    //private float[] mRotationMatrix = new float[16];

    public volatile float mVelocity;
    public static final float PLAYER_VELOCITY = 0.02f;
    public volatile long mStartTime;
    public volatile boolean duringTap;
    public static float offset = (float)(Math.random());
    public static float offset2 = (float)(Math.random());

    public void setStartTime(long startTapTime) {
        this.mStartTime = startTapTime;
    }

    public void setDuringTap(boolean duringTap) {
        this.duringTap = duringTap;
    }

    public boolean isDuringTap() {
        return duringTap;
    }

    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }

    public Context contxt;

    public GLRenderer(Context context) {
        mStartTime = 0;
        duringTap = false;
        contxt = context;
    }

    public void onPause() {
        //pause renderer
        ((MainActivity) contxt).onPause();
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
        timeOfCreation = System.currentTimeMillis();
        mSpike = new Spike(timeOfCreation, 16.98);
        mSpike2 = new Spike(timeOfCreation, 17.7777);

        // initialize the ground
        mGround = new Ground();

        mVelocity = 0f;

        mStartTime = System.currentTimeMillis();

        countSpikes = 0;

        hasCollided = false;
    }

    private boolean spike2HasStarted = false;
    private long timeOfCreation;
    private long delayTime = 5000;
    private long timeConst = 500;
    private boolean offScreen = true;
    private boolean offScreen2 = true;
    private boolean hasPassedPlayer = false;
    private boolean hasPassedPlayer2 = false;
    public static int countSpikes;
    public static boolean hasCollided;

    @Override
    public void onDrawFrame(GL10 gl) {
        // Get the current time
        long now = System.currentTimeMillis();

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
            if(elapsed >= 700 && mVelocity == PLAYER_VELOCITY) {
                mVelocity = -PLAYER_VELOCITY;
            } else if(elapsed >= 1400 && mVelocity == -PLAYER_VELOCITY) {
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

        mPlayer.movePlayer(elapsed, PLAYER_VELOCITY, duringTap);

        // END OF PLAYER RENDERING
        // STARTING SPIKE RENDERING

        float[] scratch_spike = new float[16];
        float[] scratch_spike2 = new float[16];

        // SPIKE #1
        // if the spike is off the screen, then give it an offset
        // and make it loop around
        if(mSpike.getSpikeCoords()[6] > 2.5 + offset && !offScreen) {
            offset = (float)(Math.random());
            mSpike.changeStartTime(now);
            Matrix.setIdentityM(mSpike.mModelMatrix, 0);
            Matrix.translateM(mSpike.mModelMatrix, 0, 0.01f, 0f, 0f);
            offScreen = true;
            hasPassedPlayer = false;
        } else {
            // otherwise keep moving
            // Calculate the movement of the spike
            Matrix.translateM(mSpike.mModelMatrix, 0, 0.01f, 0f, 0f);
            offScreen = false;
        }

        // SPIKE #2
        long diff = now - timeOfCreation;
        // if this is the first round, then delay the spike by some amount
        // then make it move
        if(diff >= delayTime && diff < delayTime + timeConst && !spike2HasStarted) {
            mSpike2.changeStartTime(now);
            Matrix.translateM(mSpike2.mModelMatrix, 0, 0.01f, 0f, 0f);
            spike2HasStarted = true;
        } else if(diff >= delayTime + timeConst && spike2HasStarted) {
            // if this is not the first round, then delay randomly
            // if the spike goes offscreen, make it loop around
            if(mSpike2.getSpikeCoords()[6] > 2.5 + offset2 && !offScreen2) {
                offset2 = (float)(Math.random());
                mSpike2.changeStartTime(now);
                Matrix.setIdentityM(mSpike2.mModelMatrix, 0);
                Matrix.translateM(mSpike2.mModelMatrix, 0, 0.01f, 0f, 0f);
                offScreen2 = true;
                hasPassedPlayer2 = false;
            } else {
                // Calculate the movement of the spike
                Matrix.translateM(mSpike2.mModelMatrix, 0, 0.01f, 0f, 0f);
                offScreen2 = false;
            }

            // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
            Matrix.multiplyMM(scratch_spike2, 0, mMVPMatrix, 0, mSpike2.mModelMatrix, 0);
            mSpike2.draw(scratch_spike2);
            mSpike2.moveSpike(now);
        }



        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_spike, 0, mMVPMatrix, 0, mSpike.mModelMatrix, 0);
        // draw the spike
        mSpike.draw(scratch_spike);
        mSpike.moveSpike(now);


        // if the first spike has collided
        // stop the activity
        if(mSpike.collide(mPlayer)) {
            hasCollided = true;
            onPause();
        } else if(mSpike.getSpikeCoords()[6] > 0.125f && !hasPassedPlayer) {
            hasPassedPlayer = true;
            countSpikes++;
        }
        // otherwise count how many spikes have been jumped over


        // if the second spike has collided
        // stop the activity
        if(mSpike2.collide(mPlayer)) {
            hasCollided = true;
            onPause();
        } else if(mSpike2.getSpikeCoords()[6] > 0.125f && !hasPassedPlayer2) {
            hasPassedPlayer2 = true;
            countSpikes++;
        }
        // otherwise count how many spikes have been jumped over

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

