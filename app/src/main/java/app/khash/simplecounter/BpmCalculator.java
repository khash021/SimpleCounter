package app.khash.simplecounter;

/**
 * Utility class for calculating the bpm by continuously dividing the counter (provided by the user)
 * over the total elapsed time.
 */

public class BpmCalculator {

    private final static float MINUTE_MILLI = 60000.0f;
    private long startTime = 0;
    private boolean running = false;


    //public constructor
    public BpmCalculator() {
    }//BpmCalculator

    public void start() {
        //return if the calculator is already running
        if (running) {
            return;
        }
        //get a start time and start the timer
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }//start

    /*helper method for returning the calculated bpm. The method has an argument count, which is the
      counter integer from the user
       return -1 if the counter is not running */
    public int getBpm(int count) {
        //check to make sure it is running
        if (!running) {
            return -1;
        }
        //calculate the
        long elapsedMilli = System.currentTimeMillis() - startTime;

        //calculate bpm long
        float bpmFloat = (count * MINUTE_MILLI) / elapsedMilli;

        //cast into int, we do this instead of rounding to the nearest, because a bpm of 2.9 does
        //not make sense and it is still 2.
        int bpm = (int) bpmFloat;

        return bpm;
    }//getBpm


}//BpmCalculator
