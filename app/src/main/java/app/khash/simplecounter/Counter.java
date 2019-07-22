package app.khash.simplecounter;

import android.os.Bundle;

import java.text.DecimalFormat;

/**
 * Utility class for SimpleCounter app responsible for keeping the time and bpm calculations.
 * It is responsible for providing elapsed time (stop watch) and the corresponding counter count
 * based on the specified bpm.
 */

public class Counter {

    private long startTime = 0;
    private long stopTime = 0;
    private long pauseTime = 0;
    private long pauseDiff = 0;
    private boolean running = false;
    private boolean pause = false;
    private int bpm;
    private final float MINUTE = 60.0f;
    private final static float MINUTE_MILLI = 60000.0f;
    private final static float SECOND_MILLI = 1000.0f;

    //default constructor
    public Counter() {}

    //for creating a new counter object
    public Counter(int bpm) {
        this.bpm = bpm;
    }//Counter

    //this is for creating a counter with previous values (such as orientation change)
    public Counter(Bundle savedBundle) {

        //get the info from the bundle
        this.bpm = savedBundle.getInt(MainActivity.SAVED_BPM);
        this.startTime = savedBundle.getLong(MainActivity.SAVED_START_TIME);
        this.pauseTime = savedBundle.getLong(MainActivity.SAVED_PAUSE_TIME);
        this.pauseDiff = savedBundle.getLong(MainActivity.SAVED_PAUSE_DIFF);
        this.running = true;

        String state = savedBundle.getString(MainActivity.SAVED_STATE);
        if (state.equalsIgnoreCase(MainActivity.SAVED_STATE_RUNNING)) {
            this.pause = false;
        } else if (state.equalsIgnoreCase(MainActivity.SAVED_STATE_PAUSED)) {
            this.pause = true;
        }
    }//Counter


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


    // elapsed time in milliseconds
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
            return output;
        } else {
            return null;
        }
    }//getBpmElapsed

    //returns the elapsed seconds String with three decimal points given the long input in millisec
    public static String getTestElapsedDecimalString(long elapsedMilli) {
        //result variable
        String result;

        //convert long to decimal float second
        float elapsedSec = elapsedMilli / SECOND_MILLI;

        //limit to 3 decimals
        DecimalFormat decimalFormat = new DecimalFormat("0.000");

        //convert to String with 3 decimals
        result = decimalFormat.format(elapsedSec);

        return result;
    }//getTestElapsedDecimalString

    //return elapsed seconds in 3 decimal format in String
    public String getElapsedDecimalString() {
        if (!running) {return null; }

        //get the elapsed time
        long elapsedLong = getElapsedMilli();

        return getTestElapsedDecimalString(elapsedLong);
    }//getElapsedDecimalString


    // elapsed time in seconds
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

    /* get all the information regarding the current Counter object, so it could be passed along the
       savedInstance Bundle to be used to recreate another identical object it is passed using the
       following format   (state;bpm;startTime;pauseTime;pauseDiff)
       1 for running state and 0 for paused state */
    public Bundle getSaveBundle() {
        //create and initialize a new Bundle object
        Bundle output = new Bundle();
        if(running) {
            String state;
            if (pause) {
                state = MainActivity.SAVED_STATE_PAUSED;
            } else {
                state = MainActivity.SAVED_STATE_RUNNING;
            }

            //put the data in the Bundle
            output.putString(MainActivity.SAVED_STATE, state);
            output.putInt(MainActivity.SAVED_BPM, this.bpm);
            output.putLong(MainActivity.SAVED_START_TIME, this.startTime);
            output.putLong(MainActivity.SAVED_PAUSE_TIME, this.pauseTime);
            output.putLong(MainActivity.SAVED_PAUSE_DIFF, this.pauseDiff);

            return output;

        } else {
            //this is the case that it is not running
            return null;
        }

    }//getSavingData

}//Counter Class
