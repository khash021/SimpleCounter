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

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener  {

    String TAG = getClass().getName();

    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;

    Stopwatch timer;
    final int REFRESH_RATE = 100;

    final int BPM_DEFAULT = 60;

    int mBpm;
    final int BMP_40 = 40;
    final int BMP_120 = 120;

    TextView textStopWatch, textCounter;
    EditText textBpm;
    Button buttonStart, buttonStop, buttonReset;

    RadioGroup radioBpmGroup;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    String input = timer.getBpmElapsed();
                    if (input == null) {
                        break;
                    }
                    String[] bpmElapsed = input.split(";");
                    String counter = bpmElapsed[0];
                    String elapsed = bpmElapsed[1];
                    Log.v(TAG, "C: " + counter + "; T= " + elapsed);
                    textStopWatch.setText(elapsed);
                    textCounter.setText(counter);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    timer.stop();//stop timer
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
        setContentView(R.layout.activity_main2);

        textStopWatch = findViewById(R.id.text_stop_watch);
        textCounter = findViewById(R.id.text_counter);
        textBpm = findViewById(R.id.text_bpm);

        buttonStart = findViewById(R.id.button_start);
        buttonStop = findViewById(R.id.button_stop);
        buttonReset = findViewById(R.id.button_reset);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonReset.setOnClickListener(this);

        radioBpmGroup = findViewById(R.id.radio_group_bpm);
        radioBpmGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_bpm_40:
                        mBpm = BMP_40;
                        break;
                    case R.id.radio_bpm_60:
                        mBpm = BPM_DEFAULT;
                        break;
                    case R.id.radio_bpm_120:
                        mBpm = BMP_120;
                        break;
                }//switch
            }
        });

    }//onCreate

    public void onClick(View v) {
        Log.v(TAG, "view = " + v.getTransitionName());
        if (v == buttonStart) {
            if (textBpm.getText().toString().trim().length() > 0) {
                mBpm = Integer.parseInt(textBpm.getText().toString().trim());
            } else {
                ((RadioButton) findViewById(R.id.radio_bpm_60)).setChecked(true);
            }
            timer = new Stopwatch(mBpm);
            mHandler.sendEmptyMessage(MSG_START_TIMER);
        } else if (v == buttonStop) {
            mHandler.sendEmptyMessage(MSG_STOP_TIMER);
        } else if (v == buttonReset) {
            textBpm.setText("");
            textCounter.setText("");
            textStopWatch.setText("");
        }
    }//onClick
}