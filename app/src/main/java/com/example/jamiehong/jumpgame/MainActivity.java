package com.example.jamiehong.jumpgame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;
    //private GestureDetectorCompat mDetector;

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
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            return true;
        }
    }
}
