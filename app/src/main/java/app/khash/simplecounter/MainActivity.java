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
    private final static String SAVED_BPM = "saved_bpm";
    private final static String SAVED_STATE = "saved_state";
    private final static String STATE_RUNNING = "state_running";
    private final static String STATE_PAUSED = "state_paused";
    private final static String STATE_STOPPED = "state_stopped";
    private final static String SAVED_ELAPSED = "saved_elapsed";
    private final static String SAVED_COUNTER = "saved_counter";

    Counter counter;
    //refresh every 100 mSec
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

    String mCounter, mElapsed;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_START_TIMER:
                    counter.start(); //start counter
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    String input = counter.getBpmElapsed();
                    if (input == null) {
                        Log.v(TAG, "input = null");
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                        break;
                    }
                    String[] bpmElapsed = input.split(";");
                    mCounter = bpmElapsed[0];
                    mElapsed = bpmElapsed[1];
                    Log.v(TAG, "C: " + counter + "; T= " + mElapsed);
                    textStopWatch.setText(mElapsed);
                    textCounter.setText(mCounter);
                    //update the message every REFRESH_RATE
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                    break;
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    MainActivity.this.counter.stop();//stop time
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
            //this means this a recreate and we need to retrieve our saved data and setup accordingly
            //get the elapsed and counter values and set them
            String elapsed = savedInstanceState.getString(SAVED_ELAPSED);
            String counter = savedInstanceState.getString(SAVED_COUNTER);

            //just as a precaution, lets check for null object
            if (elapsed != null) {
                mElapsed = elapsed;
                textStopWatch.setText(mElapsed);
            }
            if (counter != null) {
                mCounter = counter;
                textCounter.setText(mCounter);
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
                    counter.resume();
                    pause = false;
                    buttonStart.setEnabled(false);
                    buttonPause.setEnabled(true);
                } else if (start && !pause) {
                    //start a new one
                    counter = new Counter(mBpm);
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
                if (!(counter == null)) {
                    counter.pause();
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

        //get the values of elapsed time and counter and pass it to the bundle
        outState.putString(SAVED_ELAPSED, mElapsed);
        outState.putString(SAVED_COUNTER, mCounter);

    }//onSaveInstanceState
}