package com.example.jamiehong.jumpgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);

        ///*
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.debussy_gradus);
        mediaPlayer.start();
        //*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ///*
        mediaPlayer.stop();
        mediaPlayer.release();
        //*/
        if(((MyGLSurfaceView) mGLView).endGameCall()) {
            endGame(((MyGLSurfaceView) mGLView).mRenderer.countSpikes);
        }
    }

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
        if(countSpikes < 10) {
            message += " Better luck next time!";
        } else if(countSpikes < 20) {
            message += " Good job! Keep it up.";
        } else {
            message += " Wow! What a pro :)";
        }

        // send message to next activity
        intent.putExtra(EXTRA_MESSAGE, message);
        // start GameOverActivity
        startActivity(intent);
    }

    class MyGLSurfaceView extends GLSurfaceView {
        private final GLRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new GLRenderer(context);

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer);

            // Render the view only when there is a change in the drawing data
            //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

            // Render the view constantly (default)
            //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                if(!mRenderer.isDuringTap()) {
                    mRenderer.setVelocity(GLRenderer.PLAYER_VELOCITY);
                    //mRenderer.setStartTime(SystemClock.elapsedRealtime());
                    mRenderer.setStartTime(System.currentTimeMillis());
                    //mRenderer.setStartTime(SystemClock.uptimeMillis());
                    mRenderer.setDuringTap(true);
                }
            }
            //requestRender(); //can be used with RENDERMODE_WHEN_DIRTY
            return true;
        }

        public boolean endGameCall() {
            return mRenderer.hasCollided;
        }

        @Override
        public void onPause() {
            super.onPause();
            mRenderer.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
            mRenderer.onResume();
        }
    }
}
