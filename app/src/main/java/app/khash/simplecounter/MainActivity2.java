package app.khash.simplecounter;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    String TAG = getClass().getName();

    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;

    Stopwatch timer;
    final int REFRESH_RATE = 100;

    final int BPM_DEFAULT = 60;

    TextView textStopWatch, textCounter;
    EditText textBpm;
    Button buttonStart, buttonStop, buttonReset;

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

    }//onCreate

    public void onClick(View v) {
        if (v == buttonStart) {
            if (textBpm.getText().toString().trim().length() < 1) {
                textBpm.setText(Integer.toString(BPM_DEFAULT));
                timer = new Stopwatch(BPM_DEFAULT);
            } else {
                int bpm = Integer.parseInt(textBpm.getText().toString().trim());
                timer = new Stopwatch(bpm);
            }
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