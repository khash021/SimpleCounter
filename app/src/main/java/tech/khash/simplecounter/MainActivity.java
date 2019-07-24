package tech.khash.simplecounter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main class for SimpleCounter app
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //TODO: clean up, comment

    //constants for the handler
    private final int MSG_START_TIMER = 0;
    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;

    //constant for intent extra (for results)
    public static final int BPM_REQUEST = 1;

    //constants for the Saved Instance Bundle
    public final static String SAVED_BPM = "saved_bpm";
    public final static String SAVED_STATE = "saved_state";
    public final static String SAVED_STATE_RUNNING = "state_running";
    public final static String SAVED_STATE_PAUSED = "state_paused";
    public final static String SAVED_START_TIME = "saved_start_time";
    public final static String SAVED_PAUSE_TIME = "saved_pause_time";
    public final static String SAVED_PAUSE_DIFF = "saved_pause_diff";
    public final static String SAVED_COUNTER_BUNDLE = "saved_counter_bundle";
    public final static String SAVED_ELAPSED = "saved_elapsed";
    public final static String SAVED_COUNTER = "saved_counter";

    public final static int STATE_RESET = 1;
    public final static int STATE_RUNNING = 2;
    public final static int STATE_PAUSED = 3;
    public final static int STATE_STOPPED = 4;

    //int for tracking the status and setting up accordingly
    private int appState;

    //Handler refresh rate in ms
    final int REFRESH_RATE = 100;

    int bpm;
    final int BPM_DEFAULT = 60;
    final int BMP_40 = 40;
    final int BMP_120 = 120;

    //Counter class variable
    Counter counter;

    //views
    TextView textStopWatch, textCounter;
    EditText textBpm;
    Button buttonStart, buttonStop, buttonPause, buttonReset, buttorBpmCalc;

    RadioGroup radioBpmGroup;

    //variables to store elapsed time and counter
    String counterString, elapsedString;

    //the Handler that does the work of refreshing and getting new values from counter and updating
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
                    String input = counter.getBpmElapsed();
                    //make sure it is not null
                    if (input == null) {
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                        break;
                    }
                    //update the UI
                    String[] bpmElapsed = input.split(";");
                    counterString = bpmElapsed[0];
                    elapsedString = bpmElapsed[1];
                    textStopWatch.setText(elapsedString);
                    textCounter.setText(counterString);
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
        setContentView(R.layout.activity_main);

        //find the views
        textStopWatch = findViewById(R.id.text_timer);
        textCounter = findViewById(R.id.text_counter);

        textBpm = findViewById(R.id.text_bpm);

        buttonStart = findViewById(R.id.button_start);
        buttonPause = findViewById(R.id.button_pause);
        buttonReset = findViewById(R.id.button_reset);
        buttonStop = findViewById(R.id.button_stop);
        buttorBpmCalc = findViewById(R.id.button_calc_bpm);

        radioBpmGroup = findViewById(R.id.radio_group_bpm);

        //set click listeners
        textBpm.setOnClickListener(this);
        buttonStart.setOnClickListener(this);
        buttonPause.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttorBpmCalc.setOnClickListener(this);


        //check the savedInstanceState bundle to see whether it was an orientation change or not
        if (savedInstanceState != null) {
            /* check to see whether there is a bundle with it which means we need to create a counter
               object, otherwise we will just show the counter and stopwatch values */
            Bundle counterObjectBundle = savedInstanceState.getBundle(SAVED_COUNTER_BUNDLE);
            if (counterObjectBundle == null) {
                //this means we just need to show the saved values
                String elapsed = savedInstanceState.getString(SAVED_ELAPSED);
                String counter = savedInstanceState.getString(SAVED_COUNTER);

                //just a dummy, WTF check
                if (elapsed != null) {
                    elapsedString = elapsed;
                    textStopWatch.setText(elapsedString);
                }
                if (counter != null) {
                    counterString = counter;
                    textCounter.setText(counterString);
                }

                //setup the UI
                int state = savedInstanceState.getInt(SAVED_STATE, 0);
                //zero value means there was nothing in the bundle. Otherwise set it up
                if (state != 0) {
                    //set the app state
                    appState = state;
                    //use the helper method to setup the UI
                    setupButtons(appState);
                }//if

            } else {
                //this means we need to create a new counter object and set it up
                counter = new Counter(counterObjectBundle);

                //setup the UI
                int state = savedInstanceState.getInt(SAVED_STATE, 0);
                //zero value means there was nothing in the bundle. Otherwise set it up
                if (state != 0) {
                    //set the app state
                    appState = state;
                    //use the helper method to setup the UI
                    setupButtons(appState);
                }//if

                //if this is a pause, we also wanna just show the results
                String elapsed = savedInstanceState.getString(SAVED_ELAPSED);
                String counter = savedInstanceState.getString(SAVED_COUNTER);

                //just a dummy, WTF check
                if (elapsed != null) {
                    elapsedString = elapsed;
                    textStopWatch.setText(elapsedString);
                }
                if (counter != null) {
                    counterString = counter;
                    textCounter.setText(counterString);
                }

                //start the handler
                handler.sendEmptyMessage(MSG_UPDATE_TIMER);
            }

        } else {
            //Bundle is null and this is a new instance, so setup the default
            //set the initial state of buttons
            setupResetState();
        }

        //radio buttons click listeners
        radioBpmGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_bpm_40:
                        bpm = BMP_40;
                        textBpm.setText(Integer.toString(bpm));
                        break;
                    case R.id.radio_bpm_60:
                        bpm = BPM_DEFAULT;
                        textBpm.setText(Integer.toString(bpm));
                        break;
                    case R.id.radio_bpm_120:
                        bpm = BMP_120;
                        textBpm.setText(Integer.toString(bpm));
                        break;
                }//switch
            }
        });

    }//onCreate

    public void onClick(View v) {

        switch (v.getId()) {
            //START Button
            case R.id.button_start:
                //setup the variable bpm based on the UI
                if (textBpm.getText().toString().trim().length() > 0) {
                    bpm = Integer.parseInt(textBpm.getText().toString().trim());
                } else {
                    //by checking the 60 of the radio group, that automatically updates bpm variable
                    ((RadioButton) findViewById(R.id.radio_bpm_60)).setChecked(true);
                }

                //figure out if it is a resume or a new start
                if (appState == STATE_PAUSED) {
                    //setup resume
                    setupResumeState();
                } else if (appState == STATE_STOPPED || appState == STATE_RESET) {
                    //start a new counter and setup
                    setupRunningState();
                }
                break;

            //PAUSE button
            case R.id.button_pause:
                setupPausedState();
                break;

            //STOP button
            case R.id.button_stop:
                setupStoppedState();
                break;

            //RESET Button
            case R.id.button_reset:
                setupResetState();
                break;

            //BPM edit text
            case R.id.text_bpm:
                //enable the cursor
                textBpm.setCursorVisible(true);
                break;

            //BPM Calculator
            case R.id.button_calc_bpm:
                //start bpm calculator activity for results
                Intent bpmCalcIntent = new Intent(this, BpmCalculatorActivity.class);
                startActivityForResult(bpmCalcIntent, BPM_REQUEST);
        }//switch
    }//onClick

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //check whether it is stopped, if yes, just pass the values
        if (appState == STATE_STOPPED) {
            //get the values of elapsed time and counter and pass it to the bundle
            outState.putString(SAVED_ELAPSED, elapsedString);
            outState.putString(SAVED_COUNTER, counterString);
            //pass in the app state
            outState.putInt(SAVED_STATE, appState);

        } else if (appState == STATE_RESET) {
            //just pass in the app state so it could be setup accordingly on recreate
            outState.putInt(SAVED_STATE, appState);
        } else {
            //this means we have to get all the info and pass it along
            if (counter != null) {
                //pass along the counter info as a bundle
                outState.putBundle(SAVED_COUNTER_BUNDLE, counter.getSaveBundle());
                //add the app state so that we can setup the UI in onCreate again
                outState.putInt(SAVED_STATE, appState);
                //if this is the paused state, we wanna just pass along the numbers
                if (appState == STATE_PAUSED) {
                    outState.putString(SAVED_ELAPSED, elapsedString);
                    outState.putString(SAVED_COUNTER, counterString);
                }//if-paused
            }//if counter not null
        }
    }//onSaveInstanceState

    //Extract the calculated bpm
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //make sure it is the right result
        if (requestCode == BPM_REQUEST) {
            //make sure it is OK result
            if (resultCode == RESULT_OK) {
                int bpmCalculated = data.getIntExtra(BpmCalculatorActivity.EXTRA_REPLY_BPM, -1);
                //make sure we got something
                if (bpmCalculated != -1) {
                    //set the bpm
                    bpm = bpmCalculated;
                    textBpm.setText(Integer.toString(bpm));
                    textBpm.setCursorVisible(false);
                }
            }
        }
    }//onActivityResult

    //Initialize the contents of the Activity's standard options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //get the inflator
        MenuInflater inflater = getMenuInflater();

        //inflate the menu
        inflater.inflate(R.menu.options_menu, menu);

        //You must return true for the menu to be displayed; if you return false it will not be shown.
        return true;
    }//onCreateOptionsMenu


    /**
     * This gets called every time a menu item is clicked
     * When you successfully handle a menu item, return true.
     * If you don't handle the menu item you should call the superclass implementation
     *
     * @param item menu item that was clicked on
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get the id of the menu item
        int id = item.getItemId();

        switch (id) {
            case R.id.action_contact:
                //send email. Use Implicit intent so the user can choose their preferred app
                //create uri for email
                String email = "simplecounter@khash.tech";
                Uri emailUri = Uri.parse("mailto:" + email);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
                //make sure the device can handle the intent before sending
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                    return true;
                }
                return super.onOptionsItemSelected(item);
            case R.id.action_privacy:
                showToast("Privacy Policy");
                return true;
            case R.id.action_about:
                showToast("About");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /* ------------------HELPER METHODS------------------------   */

    //helper method for setting up the UI for reset state
    private void setupResetState() {
        //set the buttons
        setupButtonsReset();

        //reset the texts
        resetTexts();


        //make cursor visible again
        textBpm.setCursorVisible(true);

        //set app state
        appState = STATE_RESET;
    }//setupResetState

    //helper method for setting up the UI for start state (counter is running)
    private void setupRunningState() {
        //setup buttons
        setupButtonsRunning();

        //Disable the bpm text cursor
        textBpm.setCursorVisible(false);

        //start a new one
        counter = new Counter(bpm);
        handler.sendEmptyMessage(MSG_START_TIMER);

        //set app state
        appState = STATE_RUNNING;
    }//setupRunningState

    //helper method for setting up the UI for resume state
    private void setupResumeState() {
        //setup buttons
        setupButtonsRunning();

        //Disable the bpm text cursor
        textBpm.setCursorVisible(false);

        //resume counter
        counter.resume();

        //set app state
        appState = STATE_RUNNING;
    }//setupResumeState

    //helper method for setting up the UI for paused state
    private void setupPausedState() {
        //check to make sure that the counter is not null
        if (counter == null) {
            return;
        }//if

        //setup buttons
        setupButtonsPaused();

        //pause the counter (after the dummy check of counter)
        counter.pause();

        //set app state
        appState = STATE_PAUSED;
    }//setupPausedState

    //helper method for setting up the UI for stopped
    private void setupStoppedState() {
        //setup buttons
        setupButtonsStopped();

        //stop the handler
        handler.sendEmptyMessage(MSG_STOP_TIMER);

        //set app state
        appState = STATE_STOPPED;
    }//setupStoppedState

    /* Helper method that takes in the the app state as an argument and just setup the buttons and
       UI based on the app state used for when rotating rhe device */
    private void setupButtons(int appState) {
        //use a switch argument based on appState to configure buttons
        switch (appState) {
            case STATE_RUNNING:
                //setup buttons
                setupButtonsRunning();

                //Disable the bpm text cursor
                textBpm.setCursorVisible(false);
                break;
            case STATE_PAUSED:
                //setup buttons
                setupButtonsPaused();

                //Disable the bpm text cursor
                textBpm.setCursorVisible(false);
                break;
            case STATE_STOPPED:
                //setup buttons
                setupButtonsStopped();
                break;
            case STATE_RESET:
                //set the buttons
                setupButtonsReset();

                //reset the texts
                resetTexts();

                //make cursor visible again
                textBpm.setCursorVisible(true);
                break;
        }//switch
    }//setupButtons

    //helper method for resetting all the texts
    private void resetTexts() {
        textBpm.setText("");
        textCounter.setText("");
        textStopWatch.setText("");
    }//resetTexts

    //Helper method for setting up buttons for reset state
    private void setupButtonsReset() {
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(false);

        buttorBpmCalc.setEnabled(true);

        //change start button text
        buttonStart.setText(R.string.start);
    }//setupButtonsReset

    //Helper method for setting up buttons for running state
    private void setupButtonsRunning() {
        buttonStart.setEnabled(false);
        buttonPause.setEnabled(true);
        buttonStop.setEnabled(true);
        buttonReset.setEnabled(false);

        buttorBpmCalc.setEnabled(false);
    }//setupButtonsRunning

    //Helper method for setting up buttons for paused state
    private void setupButtonsPaused() {
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(true);
        buttonReset.setEnabled(true);

        buttorBpmCalc.setEnabled(false);

        //change the start button text
        buttonStart.setText(R.string.resume);
    }//setupButtonsPaused

    //Helper method for setting up buttons for stopped state
    private void setupButtonsStopped() {
        buttonStop.setEnabled(false);
        buttonPause.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonReset.setEnabled(true);

        buttorBpmCalc.setEnabled(false);
    }//setupButtonsStopped

}//MainActivity