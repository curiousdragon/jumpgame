package com.example.jamiehong.jumpgame;

import android.opengl.Matrix;
import android.os.SystemClock;

import java.util.ArrayList;


public class SpikeGenerator {
    public static ArrayList<Spike> list;

    public class Pair {
        public Spike spike;
        public float[] coord;

        public Pair (Spike s, float[] f) {
            spike = s;
            coord = f;
        }
    }

    public SpikeGenerator(){
        list = new ArrayList<Spike>();
    }


    public void draw(float[] mvpMatrix) {
        if ((int)(Math.random() * 10) == 5) {
            list.add(new Spike());
        }
    }

}


