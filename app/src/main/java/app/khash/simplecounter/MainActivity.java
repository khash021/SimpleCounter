package app.khash.simplecounter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO: add comments and start cleaning up the code and variables
    //TODO: group reset and other tasks into separate functions for the buttons

    String TAG = getClass().getName();

    //constants for the handler
    private final int MSG_START_TIMER = 0;
    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;

    //constants for the Saved Instance Bundle
    public final static String SAVED_BPM = "saved_bpm";
    public final static String SAVED_STATE = "saved_state";
    public final static String STATE_RUNNING = "state_running";
    public final static String STATE_PAUSED = "state_paused";
    public final static String SAVED_START_TIME = "saved_start_time";
    public final static String SAVED_PAUSE_TIME = "saved_pause_time";
    public final static String SAVED_PAUSE_DIFF = "saved_pause_diff";
    private final static String SAVED_COUNTER_BUNDLE = "saved_counter_bundle";
    private final static String SAVED_ELAPSED = "saved_elapsed";
    private final static String SAVED_COUNTER = "saved_counter";

    //Counter class variable
    Counter mCounter;

    //Handler refresh rate in ms
    final int REFRESH_RATE = 100;

    int mBpm;
    final int BPM_DEFAULT = 60;
    final int BMP_40 = 40;
    final int BMP_120 = 120;

    TextView textStopWatch, textCounter;
    EditText textBpm;
    Button buttonStart, buttonStop, buttonReset, buttonPause;

    RadioGroup radioBpmGroup;

    final String START = "START";
    final String RESUME = "RESUME";

    boolean pause = false;
    boolean start = true;

    String mCounterString, mElapsedString;


    //the Handler that does the work of refreshing and getting new values from counter and updating
    //the UI
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                //start the timer (Here we start the counter) and start updating
                case MSG_START_TIMER:
                    mCounter.start(); //start mCounter
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                //this is where we constantly get the elapsed from counter and update UI
                case MSG_UPDATE_TIMER:
                    //Get the bpm and elapsed tim from the counter
                    String input = mCounter.getBpmElapsed();
                    //make sure it is not null
                    if (input == null) {
                        Log.v(TAG, "input = null");
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                        break;
                    }
                    //update the UI
                    String[] bpmElapsed = input.split(";");
                    mCounterString = bpmElapsed[0];
                    mElapsedString = bpmElapsed[1];
                    Log.v(TAG, "C: " + mCounter + "; T= " + mElapsedString);
                    textStopWatch.setText(mElapsedString);
                    textCounter.setText(mCounterString);
                    //update the message every REFRESH_RATE
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                    break;
                //Stop the handler
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    mCounter.stop();//stop time
                    break;

                default:
                    break;
            }
        }
    };//handler


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find the views
        textStopWatch = findViewById(R.id.text_timer);
        textCounter = findViewById(R.id.text_counter);

        textBpm = findViewById(R.id.text_bpm);

        buttonStart = findViewById(R.id.button_start);
        buttonStop = findViewById(R.id.button_stop);
        buttonReset = findViewById(R.id.button_reset);
        buttonPause = findViewById(R.id.button_pause);

        radioBpmGroup = findViewById(R.id.radio_group_bpm);

        //set click listeners
        textBpm.setOnClickListener(this);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttonPause.setOnClickListener(this);

        //check the savedInstanceState bundle to see whether it was an orientation change or not
        if (savedInstanceState != null) {
            //check to see whether there is a bundle with it which means we need to create a counter
            //object, otherwise we will just show the counter and stopwatch values
            Bundle counterObjectBundle = savedInstanceState.getBundle(SAVED_COUNTER_BUNDLE);
            if (counterObjectBundle == null) {
                //this means we just need to show the saved values
                String elapsed = savedInstanceState.getString(SAVED_ELAPSED);
                String counter = savedInstanceState.getString(SAVED_COUNTER);

                //just a dummy, WTF check
                if (elapsed != null) {
                    mElapsedString = elapsed;
                    textStopWatch.setText(mElapsedString);
                }
                if (counter != null) {
                    mCounterString = counter;
                    textCounter.setText(mCounterString);
                }

            } else {
                //this means we need to create a new counter object and set it up
                mCounter = new Counter(counterObjectBundle);

                //start the handler
                mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);

            }


        } else {
            //Bundle is null and this is a new instance, so setup the default
            //set the initial state of buttons
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
            buttonReset.setEnabled(false);
            buttonPause.setEnabled(false);
        }

        //radio buttons click listeners
        radioBpmGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_bpm_40:
                        mBpm = BMP_40;
                        textBpm.setText(Integer.toString(mBpm));
                        break;
                    case R.id.radio_bpm_60:
                        mBpm = BPM_DEFAULT;
                        textBpm.setText(Integer.toString(mBpm));
                        break;
                    case R.id.radio_bpm_120:
                        mBpm = BMP_120;
                        textBpm.setText(Integer.toString(mBpm));
                        break;
                }//switch
            }
        });

    }//onCreate

    public void onClick(View v) {

        switch (v.getId()) {
            //TODO: display resume once it is in pause mode
            //START Button
            case R.id.button_start:
                if (textBpm.getText().toString().trim().length() > 0) {
                    mBpm = Integer.parseInt(textBpm.getText().toString().trim());
                } else {
                    ((RadioButton) findViewById(R.id.radio_bpm_60)).setChecked(true);
                }
                //Disable the bpm text cursor
                textBpm.setCursorVisible(false);
                if (!start && pause) {
                    //resume
                    mCounter.resume();
                    pause = false;
                    buttonStart.setEnabled(false);
                    buttonPause.setEnabled(true);
                } else if (start && !pause) {
                    //start a new one
                    mCounter = new Counter(mBpm);
                    mHandler.sendEmptyMessage(MSG_START_TIMER);
                    buttonStart.setEnabled(false);
                    buttonPause.setEnabled(true);
                    buttonStop.setEnabled(true);
                    buttonReset.setEnabled(false);
                    start = false;
                }
                break;

            //PAUSE butoon
            case R.id.button_pause:
                if (!(mCounter == null)) {
                    mCounter.pause();
                    buttonPause.setEnabled(false);
                    buttonStart.setEnabled(true);
                    buttonReset.setEnabled(true);
                    buttonStop.setEnabled(true);
                    pause = true;
                    buttonStart.setText(RESUME);
                }
                break;

            //STOP button
            case R.id.button_stop:
                mHandler.sendEmptyMessage(MSG_STOP_TIMER);

                buttonStop.setEnabled(false);
                buttonPause.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonReset.setEnabled(true);
                break;

            //RESET Button
            case R.id.button_reset:
                textBpm.setText("");
                //make cursor visible again
                textBpm.setCursorVisible(true);
                textCounter.setText("");
                textStopWatch.setText("");

                buttonStart.setEnabled(true);
                buttonPause.setEnabled(false);
                buttonStop.setEnabled(false);
                buttonReset.setEnabled(false);
                buttonStart.setText(START);
                pause = false;
                start = true;
                break;

            //BPM edit text
            case R.id.text_bpm:
                //enable the cursor
                textBpm.setCursorVisible(true);
                break;

        }//switch
    }//onClick

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //check whether it is stopped, if yes, just pass the values
        if (!buttonStop.isEnabled()) {
            //get the values of elapsed time and mCounter and pass it to the bundle
            outState.putString(SAVED_ELAPSED, mElapsedString);
            outState.putString(SAVED_COUNTER, mCounterString);
        } else {
            //this means we have to get all the info and pass it along
            if (mCounter != null) {
                outState.putBundle(SAVED_COUNTER_BUNDLE, mCounter.getSaveBundle());
            }
        }

    }//onSaveInstanceState
}