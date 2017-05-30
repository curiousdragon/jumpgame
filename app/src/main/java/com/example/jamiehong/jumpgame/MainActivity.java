package com.example.jamiehong.jumpgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    // declare variables
    private GLSurfaceView mGLView; // used to display graphics
    private MediaPlayer mediaPlayer; // used to play music

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // create
        super.onCreate(savedInstanceState);
        // declare mGLView
        mGLView = new MyGLSurfaceView(this);
        // set the content view using OpenGL ES
        setContentView(mGLView);

        // create a media player
        // it plays Doctor Gradus ad Parnassum, by Debussy
        // from online free source as mp3
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.debussy_gradus);
        // start playing the music once gameplay starts
        mediaPlayer.start();
    }

    // resume method
    @Override
    protected void onResume() {
        super.onResume();
    }

    // pause method
    @Override
    protected void onPause() {
        super.onPause();
        // stop the mediaPlayer
        mediaPlayer.stop();
        mediaPlayer.release();

        // call the endGame method
        // pass in parameter how many spikes player has jumped over
        if(((MyGLSurfaceView) mGLView).endGameCall()) { // if the user has collided
            // then end the game
            endGame(((MyGLSurfaceView) mGLView).mRenderer.countSpikes);
        }
    }

    // used in endGame()
    // used in the intent that starts the GameOverActivity
    public static final String EXTRA_MESSAGE = "com.example.jamiehong.MESSAGE";

    public void endGame(int countSpikes) {
        // create intent for GameOverActivity
        Intent intent = new Intent(this, GameOverActivity.class);

        // ending message
        String message = "You successfully jumped over " + countSpikes;
        // grammar specifics
        if(countSpikes == 1) {
            message += " spike!";
        } else {
            message += " spikes!";
        }

        // encouragement
        if(countSpikes < 8) {
            message += " Better luck next time!";
        } else if(countSpikes < 16) {
            message += " Good job! Keep it up.";
        } else {
            message += " Wow! What a pro :)";
        }

        // send message to next activity
        intent.putExtra(EXTRA_MESSAGE, message);
        // start GameOverActivity
        startActivity(intent);
    }

    // surface view class for using GLRenderer
    class MyGLSurfaceView extends GLSurfaceView {
        // GLRenderer variable
        private final GLRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            // declare mRenderer as a GLRenderer
            mRenderer = new GLRenderer(context);

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // if the user taps the screen
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                // if the user's previous tap is no longer in effect
                if(!mRenderer.isDuringTap()) {
                    // set the velocity of the player green square
                    // using the constant in GLRenderer
                    mRenderer.setVelocity(GLRenderer.PLAYER_VELOCITY);
                    // set the start time of the tap as the current time
                    mRenderer.setStartTime(System.currentTimeMillis());
                    // makes sure that the next tap will not make
                    // the player double jump
                    // while this tap is still in effect
                    mRenderer.setDuringTap(true);
                }
            }
            return true;
        }

        // used to determine when to call endGame()
        public boolean endGameCall() {
            // if the player has collided with a spike
            // then end the game
            // otherwise, keep playing
            return mRenderer.hasCollided;
        }

        // pause method
        @Override
        public void onPause() {
            super.onPause();
            mRenderer.onPause();
        }

        // resume method
        @Override
        public void onResume() {
            super.onResume();
            mRenderer.onResume();
        }
    }
}
