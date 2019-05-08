package app.khash.simplecounter;

import android.util.Log;

public class Stopwatch {

    String TAG = getClass().getName();

    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;
    private int bpm;
    private final float MINUTE = 60.0f;

    public Stopwatch(int bpm) {
        this.bpm = bpm;
    }//Stopwatch


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }//start


    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }//stop


    // elaspsed time in milliseconds
    public long getElapsedMilli() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return stopTime - startTime;
    }//getElapsedMilli


    // elaspsed time in seconds
    public long getElapsedSecs() {
        if (running) {
            return ((System.currentTimeMillis() - startTime) / 1000);
        }
        return ((stopTime - startTime) / 1000);
    }//getElapsedSecs

    //get counter value based on bpm
    public int getBpm() {
        if (running) {
            long elapsedMilli = getElapsedMilli();
            float bpmFloat = elapsedMilli * (bpm / MINUTE);
            int counter = Math.round(bpmFloat / 1000);
            return counter;

        } else {
            return -1;
        }
    }//getBpm

    //get the time and bpm as to avoid the difference between the two
    public String getBpmElapsed() {
        if (running) {
            long elapsedMilli = getElapsedMilli();
            float bpmFloat = elapsedMilli * (bpm / MINUTE);
            int counter = (int) bpmFloat/1000;
            String output = counter + ";" + (elapsedMilli /1000);
            Log.v(TAG, elapsedMilli + " : " + bpmFloat + " : " + counter);
            return output;
        } else {
            return null;
        }

    }//getBpmElapsed

}
