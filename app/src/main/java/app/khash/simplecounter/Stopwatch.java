package app.khash.simplecounter;

import android.util.Log;

public class Stopwatch {

    String TAG = getClass().getName();

    private long startTime = 0;
    private long stopTime = 0;
    private long pauseTime = 0;
    private long pauseDiff = 0;
    private boolean running = false;
    private boolean pause = false;
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

    public void pause() {
        if (running) {
            this.pauseTime = System.currentTimeMillis();
            pause = true;
        }
    }//pause

    public void resume() {
        if (running) {
            this.pauseDiff = (System.currentTimeMillis() - pauseTime) + pauseDiff;
            pause = false;
        }
    }//resume


    // elaspsed time in milliseconds
    public long getElapsedMilli() {
        if (running) {
            return (System.currentTimeMillis() -pauseDiff) - startTime;
        }
        return stopTime - startTime;
    }//getElapsedMilli

    //get the time and bpm as to avoid the difference between the two
    public String getBpmElapsed() {
        if (pause) {
            return null;
        }
        if (running) {
            long elapsedMilli = getElapsedMilli();
            float bpmFloat = elapsedMilli * (bpm / MINUTE);
            int counter = (int) bpmFloat/1000;
            String output = counter + ";" + (elapsedMilli /1000);
            Log.v(TAG, "E-" + elapsedMilli + " : " + "P-" + pauseDiff + " : " +
                    "BPM-" + bpmFloat + " : " + "C-" + counter);
            return output;
        } else {
            return null;
        }
    }//getBpmElapsed


    // elaspsed time in seconds
    public long getElapsedSecs() {
        if (running) {
            return (((System.currentTimeMillis() -pauseDiff) - startTime) / 1000);
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



}
