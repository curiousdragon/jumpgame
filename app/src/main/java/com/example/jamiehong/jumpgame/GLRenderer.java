package com.example.jamiehong.jumpgame;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    // variables for what is to be drawn
    private Player mPlayer;
    private Spike mSpike;
    private Spike mSpike2;
    private Ground mGround;

    // variables for projection
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    // variables for player
    public volatile float mVelocity;
    // current velocity of player
    public static final float PLAYER_VELOCITY = 0.02f;
    // player velocity constant
    public volatile long mStartTime;
    // start time of current tap
    public volatile boolean duringTap;
    // makes sure no tapping overlaps

    // variables for spikes
    public static float offset = (float)(Math.random());
    // dist offset for mSpike
    public static float offset2 = (float)(Math.random());
    // dist offset for mSpike2

    // set method for beginning time of tap
    public void setStartTime(long startTapTime) {
        this.mStartTime = startTapTime;
    }

    // set method for making sure no tapping overlaps
    public void setDuringTap(boolean duringTap) {
        this.duringTap = duringTap;
    }

    // get method for whether user's previous tap is still in effect
    public boolean isDuringTap() {
        return duringTap;
    }

    // set method for player's velocity
    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }

    // used to start GameOverActivity when needed
    // able to call MainActivity's endGame method
    public Context contxt;

    // constructor
    public GLRenderer(Context context) {
        // make mStartTime 0 so it doesn't affect anything
        // until the user taps
        mStartTime = 0;

        // user has not tapped yet
        duringTap = false;

        // set the context
        contxt = context;
    }

    public void onPause() {
        //pause renderer
        ((MainActivity) contxt).onPause();
        // checks to see if it is time to end the game
        // if so then MainActivity will start GameOverActivity
    }

    public void onResume() {
        //resume renderer
        return;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a player
        mPlayer = new Player();

        // initialize 2 spikes
        timeOfCreation = System.currentTimeMillis();
        mSpike = new Spike(timeOfCreation, 16.98);
        mSpike2 = new Spike(timeOfCreation, 17.8);

        // initialize the ground
        mGround = new Ground();

        // initialize the velocity of player
        mVelocity = 0f;

        // set the current tap start time as now
        mStartTime = System.currentTimeMillis();

        // user hasn't jumped over any spikes yet, so 0
        countSpikes = 0;

        // user hasn't collided yet, so false
        hasCollided = false;
    }

    // variables used for spike
    private boolean spike2HasStarted = false;
    // has the 2nd spike started moving yet
    // this variable only used at the beginning of the game
    // to make sure there is an offset between the first
    // and second spikes (mSpike and mSpike2 respectively)
    private long timeOfCreation;
    // the time when the game first started
    // used to figure out spike coordinates
    private long delayTime = 5000;
    // how long the second spike has to wait
    // at the beginning of the game
    // until following the first spike
    private long timeConst = 500;
    // gives a range for delay time
    // since difficult for the spike time to be precise
    private boolean offScreen = true;
    // has the first spike gone off screen
    private boolean offScreen2 = true;
    // has the second spike gone off screen
    private boolean hasPassedPlayer = false;
    // is the first spike to the left of the player
    private boolean hasPassedPlayer2 = false;
    // is the second spike to left of the player

    // static variables for spike
    public static int countSpikes;
    // # spikes the player has jumped over
    public static boolean hasCollided;
    // has the player collided with a spike

    @Override
    public void onDrawFrame(GL10 gl) {
        // Get the current time
        long now = System.currentTimeMillis();

        // We should make sure we are valid and sane
        if (mStartTime > now) return;

        // Get the amount of time the last frame took.
        long elapsed = now - mStartTime;

        // used to calculate movement + projection for player
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

        // calculate the player's movement
        Matrix.translateM(mPlayer.mModelMatrix, 0, 0f, mVelocity, 0f);

        // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
        Matrix.multiplyMM(scratch_player, 0, mMVPMatrix, 0, mPlayer.mModelMatrix, 0);
        // draw the player
        mPlayer.draw(scratch_player);

        // calculate the coordinates of the player
        mPlayer.movePlayer(elapsed, PLAYER_VELOCITY, duringTap);

        // END OF PLAYER RENDERING
        // STARTING SPIKE RENDERING

        // used to calculate movement + projection for mSpike and mSpike2
        float[] scratch_spike = new float[16];
        float[] scratch_spike2 = new float[16];

        // SPIKE #1
        // if the spike is off the screen, then give it an offset
        // and make it loop around
        if(mSpike.getSpikeCoords()[6] > 2.5 + offset && !offScreen) {
            // create a random dist. offset
            offset = (float)(Math.random());
            // change the startTime to the current time so it's easier to
            // calculate the coordinates later
            mSpike.changeStartTime(now);
            Matrix.setIdentityM(mSpike.mModelMatrix, 0);
            Matrix.translateM(mSpike.mModelMatrix, 0, Spike.velocity, 0f, 0f);
            offScreen = true;
            hasPassedPlayer = false;
        } else {
            // otherwise keep moving
            // Calculate the movement of the spike
            Matrix.translateM(mSpike.mModelMatrix, 0, Spike.velocity, 0f, 0f);
            offScreen = false;
        }

        // SPIKE #2
        long diff = now - timeOfCreation;
        // if this is the first round, then delay the spike by some amount
        // then make it move
        if(diff >= delayTime && diff < delayTime + timeConst && !spike2HasStarted) {
            // change the start Time to the current Time for calculating coordinates
            mSpike2.changeStartTime(now);
            // have the spike start moving
            Matrix.translateM(mSpike2.mModelMatrix, 0, Spike.velocity, 0f, 0f);
            spike2HasStarted = true;
        } else if(diff >= delayTime + timeConst && spike2HasStarted) {
            // if this is not the first round, then delay randomly
            // if the spike goes offscreen, make it loop around
            if(mSpike2.getSpikeCoords()[6] > 2.5 + offset2 && !offScreen2) {
                offset2 = (float)(Math.random());
                mSpike2.changeStartTime(now);
                Matrix.setIdentityM(mSpike2.mModelMatrix, 0);
                Matrix.translateM(mSpike2.mModelMatrix, 0, Spike.velocity, 0f, 0f);
                offScreen2 = true;
                hasPassedPlayer2 = false;
            } else {
                // Calculate the movement of the spike
                Matrix.translateM(mSpike2.mModelMatrix, 0, Spike.velocity, 0f, 0f);
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
        // calculate the spike coordinates
        mSpike.moveSpike(now);


        // if the first spike has collided
        // stop the activity
        if(mSpike.collide(mPlayer)) {
            hasCollided = true;
            // stop the activity
            onPause();
        } else if(mSpike.getSpikeCoords()[6] > 0.125f + 0.002f && !hasPassedPlayer) {
            hasPassedPlayer = true;
            // increment the number of spikes the player has jumped over
            countSpikes++;
        }
        // otherwise count how many spikes have been jumped over


        // if the second spike has collided
        // stop the activity
        if(mSpike2.collide(mPlayer)) {
            hasCollided = true;
            onPause();
        } else if(mSpike2.getSpikeCoords()[6] > 0.12f + 0.002f && !hasPassedPlayer2) {
            hasPassedPlayer2 = true;
            // increment the number of spikes the player has jumped over
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

