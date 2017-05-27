package com.example.jamiehong.jumpgame;

import android.opengl.Matrix;
import android.os.SystemClock;

import java.util.ArrayList;


public class SpikeGenerator {

    public volatile ArrayList<Spike> list;
    private static long mCreationTime;
    private long mCurrentTime;
    private long mTimeElapsed;
    private long mRandTime;
    public long mPrevSpike;
    public static int mScore;

    public SpikeGenerator(long time){
        list = new ArrayList<Spike>();
        //mCreationTime = time;
        mCreationTime = System.currentTimeMillis();
        mCurrentTime = 0;
        mTimeElapsed = 0;
        mPrevSpike = mCreationTime;
        mRandTime = (long)(Math.random() * 2000) + 500;
        mScore = 0;
    }

    public void draw(float[] mvpMatrix) {
        mCurrentTime = System.currentTimeMillis();
        mTimeElapsed = mCurrentTime - mPrevSpike;
        float[] scratch_spike = new float[16];

        add();

        for (int i = 0; i < list.size(); i ++) {
            Spike s = list.get(i);
            // Calculate the movement of the spike
            Matrix.translateM(s.mModelMatrix, 0, s.velocity, 0f, 0f);
            // Calculate: will only be different from mMVPMatrix if mModelMatrix has changed
            Matrix.multiplyMM(scratch_spike, 0, mvpMatrix, 0, s.mModelMatrix, 0);
            s.draw(scratch_spike);
            s.moveSpike(mCurrentTime);
        }

        remove();
    }

    public void add() {
        if (mTimeElapsed > mRandTime) {
            //list.add(new Spike(mCurrentTime));
            mRandTime = (long)(Math.random() * 2000) + 500;
            mPrevSpike = mCurrentTime;
        }
    }

    public Spike remove() {
        if (list.size() < 1) return null;
        Spike s = list.get(0);
        if (s.getSpikeCoords()[6] > 2.5) {
            list.remove(0);
            mScore ++;
            return s;
        }
        return null;
    }

    public boolean collide(Player p) {
        for(Spike s : list) {
            if(s.collide(p)) {
                return true;
            }
        }
        return false;
    }

}


