package app.khash.simplecounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Class for helping the user find the bpm
 */

public class BpmCalculatorActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonStart, buttonStop, buttonReset, buttonDone, buttonCounterIncrease;
    private TextView textTimer, textCounter;

    //to keep track of the timer
    private int counter;

    //constants to keep track of app state to be used for rotation events
    private final static int STATE_RESET = 1;
    private final static int STATE_RUNNING = 2;
    private final static int STATE_STOPEED = 3;
    private int appState;

    //constants for the handler
    private final int MSG_START_TIMER = 0;
    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;

    //Handler refresh rate in ms
    final int REFRESH_RATE = 100;

    //BPM Calculator class variable
    BpmCalculator bpmCalculator;

    //the Handler that does the work of refreshing and getting new values from  and updating
    //the UI
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                //start the timer (Here we start the counter) and start updating
//                case MSG_START_TIMER:
//                    counter.start(); //start counter
//                    handler.sendEmptyMessage(MSG_UPDATE_TIMER);
//                    break;
//
//                //this is where we constantly get the elapsed from counter and update UI
//                case MSG_UPDATE_TIMER:
//                    //Get the bpm and elapsed tim from the counter
//                    String input = counter.getBpmElapsed();
//                    //make sure it is not null
//                    if (input == null) {
//                        handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
//                        break;
//                    }
//                    //update the UI
//                    String[] bpmElapsed = input.split(";");
//                    counterString = bpmElapsed[0];
//                    elapsedString = bpmElapsed[1];
//                    textStopWatch.setText(elapsedString);
//                    textCounter.setText(counterString);
//                    //update the message every REFRESH_RATE
//                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
//                    break;
//                //Stop the handler
//                case MSG_STOP_TIMER:
//                    handler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
//                    //WTF dummy check in case there is no counter object
//                    if (counter != null) {
//                        counter.stop();//stop time
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };//handler


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm_calculator);

        //find views
        textTimer = findViewById(R.id.text_timer);
        textCounter = findViewById(R.id.text_counter);

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


    }//onCreate



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }//switch

    }//onClick


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
    }//setupButtonsReset

    //Helper method for setting up buttons for running state
    private void setupButtonsRunning() {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        buttonReset.setEnabled(false);
        buttonCounterIncrease.setEnabled(true);
        buttonDone.setEnabled(false);
    }//setupButtonsRunning

    //Helper method for setting up buttons for stopped state
    private void setupButtonsStopped() {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(true);
        buttonCounterIncrease.setEnabled(false);
        buttonDone.setEnabled(true);
    }//setupButtonsStopped

    //helper method for resetting all the texts
    private void resetTexts() {
        textTimer.setText(R.string.zero_decimal);
        textCounter.setText(R.string.zero);
    }//resetTexts

}//BpmCalculatorActivity
