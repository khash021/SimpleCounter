package app.khash.simplecounter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Class for helping the user find the bpm
 */

public class BpmCalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO: SavedInstances
    //TODO: Handle special cases for bpm (no timer, no count)


    private Button buttonStart, buttonStop, buttonReset, buttonDone, buttonCounterIncrease;
    private TextView textTimer, textCounter, textBpm, textBpmSuffix;

    //to keep track of the timer
    private int count;
    private long elapsedLong;
    private int calculatedBpm = -1;

    //constants to keep track of app state to be used for rotation events
    private final static int STATE_RESET = 1;
    private final static int STATE_RUNNING = 2;
    private final static int STATE_STOPPED = 3;
    private int appState;

    public static final String EXTRA_REPLY_BPM = "extra_reply_bpm";

    //constants for the handler
    private final int MSG_START_TIMER = 0;
    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;

    //Handler refresh rate in ms
    final int REFRESH_RATE = 100;

    //Counter object
    Counter counter;

    //the Handler that does the work of refreshing and getting new values from  and updating
    //the UI
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                //start the timer (Here we start the counter) and start updating
                case MSG_START_TIMER:
                    counter.start(); //start counter
                    handler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                //this is where we constantly get the elapsed from counter and update UI
                case MSG_UPDATE_TIMER:
                    //Get the bpm and elapsed tim from the counter
                    String elapsed = counter.getElapsedDecimalString();
                    elapsedLong = counter.getElapsedMilli();
                    //make sure it is not null
                    if (elapsed == null) {
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                        break;
                    }
                    //update the UI
                    textTimer.setText(elapsed);
                    //update the message every REFRESH_RATE
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                    break;
                //Stop the handler
                case MSG_STOP_TIMER:
                    handler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    //WTF dummy check in case there is no counter object
                    if (counter != null) {
                        counter.stop();//stop time
                    }
                    break;

                default:
                    break;
            }
        }
    };//handler


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm_calculator);

        //find views
        textTimer = findViewById(R.id.text_timer);
        textCounter = findViewById(R.id.text_counter);
        textBpm = findViewById(R.id.text_bpm);
        textBpmSuffix = findViewById(R.id.text_bpm_suffix);

        textBpmSuffix.setVisibility(View.INVISIBLE);

        buttonStart = findViewById(R.id.button_start);
        buttonStop = findViewById(R.id.button_stop);
        buttonReset = findViewById(R.id.button_reset);
        buttonDone = findViewById(R.id.button_done);
        buttonCounterIncrease = findViewById(R.id.button_counter_increase);

        //set on click listeners on views
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttonDone.setOnClickListener(this);
        buttonCounterIncrease.setOnClickListener(this);

        //TODO: figure out the savedInstances

        resetTexts();

    }//onCreate



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //increase the count by one
            case R.id.button_counter_increase:
                count++;
                textCounter.setText(Integer.toString(count));
                break;
            //start button
            case R.id.button_start:
                setupButtonsRunning();
                resetTexts();
                counter = new Counter();
                counter.start();
                handler.sendEmptyMessage(MSG_START_TIMER);
                appState = STATE_RUNNING;
                break;
            //stop button
            case R.id.button_stop:
                handler.sendEmptyMessage(MSG_STOP_TIMER);
                setupButtonsStopped();
                int bpm = getBpm(count);
                //returns -1 if there was a problem
                if (bpm != -1) {
                    textBpm.setText(Integer.toString(bpm));
                }
                appState = STATE_STOPPED;
                break;
            //reset button
            case R.id.button_reset:
                setupButtonsReset();
                resetTexts();
                count = 0;
                appState = STATE_RESET;
                break;
            //button done
            case R.id.button_done:
                //TODO: send data back
                if (calculatedBpm == -1) {
                    break;
                }
                Intent bpmIntent = new Intent();
                bpmIntent.putExtra(EXTRA_REPLY_BPM, calculatedBpm);
                setResult(RESULT_OK, bpmIntent);
                finish();

        }//switch

    }//onClick

    /**  helper method for returning the calculated bpm. The method has an argument count,
     *   which is the counter integer from the user (count)
         returns -1 if the counter is not running */
    public int getBpm(int count) {
        //check to make sure count is not zero
        if (count < 1) {
            return -1;
        }

        float MINUTE_MILLI = 60000.0f;
        //calculate bpm long
        float bmpFloat = (count * MINUTE_MILLI) / elapsedLong;

        //cast into int, we do this instead of rounding to the nearest, because a bpm of 2.9 does
        //not make sense and it is still 2.
        int bpm = (int) bmpFloat;

        calculatedBpm = bpm;

        return bpm;
    }//getBpm


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }//onSaveInstanceState

    //Helper method for setting up buttons for reset state
    private void setupButtonsReset() {
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(false);
        buttonCounterIncrease.setEnabled(false);
        buttonDone.setEnabled(false);
        textBpmSuffix.setVisibility(View.INVISIBLE);
    }//setupButtonsReset

    //Helper method for setting up buttons for running state
    private void setupButtonsRunning() {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        buttonReset.setEnabled(false);
        buttonCounterIncrease.setEnabled(true);
        buttonDone.setEnabled(false);
        textBpmSuffix.setVisibility(View.INVISIBLE);
    }//setupButtonsRunning

    //Helper method for setting up buttons for stopped state
    private void setupButtonsStopped() {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(true);
        buttonCounterIncrease.setEnabled(false);
        buttonDone.setEnabled(true);
        textBpmSuffix.setVisibility(View.VISIBLE);
    }//setupButtonsStopped

    //helper method for resetting all the texts
    private void resetTexts() {
        textTimer.setText(R.string.zero_decimal);
        textCounter.setText(R.string.zero);
        textBpm.setText("");
    }//resetTexts

}//BpmCalculatorActivity
