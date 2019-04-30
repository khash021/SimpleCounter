package app.khash.simplecounter;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    String TAG = getClass().getName();

    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;

    Stopwatch timer = new Stopwatch();
    final int REFRESH_RATE = 100;

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    tvTextView.setText(""+ timer.getElapsedTimeSecs());
                    Log.v(TAG, Long.toString(timer.getElapsedTimeSecs()));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER,REFRESH_RATE); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    timer.stop();//stop timer
                    tvTextView.setText(""+ timer.getElapsedTimeSecs());
                    break;

                default:
                    break;
            }
        }
    };

    TextView tvTextView;
    Button btnStart,btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tvTextView = findViewById(R.id.text_timer);

        btnStart = findViewById(R.id.button_start);
        btnStop= findViewById(R.id.button_stop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

    }

    public void onClick(View v) {
        if(btnStart == v)
        {
            mHandler.sendEmptyMessage(MSG_START_TIMER);
        }else
        if(btnStop == v){
            mHandler.sendEmptyMessage(MSG_STOP_TIMER);
        }
    }
}