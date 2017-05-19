package com.example.jamiehong.jumpgame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView {
        private final GLRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new GLRenderer();

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
