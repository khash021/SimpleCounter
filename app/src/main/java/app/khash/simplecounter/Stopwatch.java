package app.khash.simplecounter;

public class Stopwatch {

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
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return stopTime - startTime;
    }//getElapsedTime


    // elaspsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            return ((System.currentTimeMillis() - startTime) / 1000);
        }
        return ((stopTime - startTime) / 1000);
    }//getElapsedTimeSecs

    //get counter value based on bpm
    public int getBpm() {
        if (running) {
            long elapsedMilli = getElapsedTime();
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
            long elapsedMilli = getElapsedTime();
            float bpmFloat = elapsedMilli * (bpm / MINUTE);
            int counter = Math.round(bpmFloat / 1000);
            String output = counter + ";" + elapsedMilli;
            return output;
        } else {
            return null;
        }

    }//getBpmElapsed

}
