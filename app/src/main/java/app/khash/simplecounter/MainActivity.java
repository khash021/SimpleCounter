package app.khash.simplecounter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String TAG = getClass().getName();

    TextView mTimerText;
    Boolean mStart;
    Button mStartButton;
    CountDownTimer mCountdownTimer;
    final Long END = 999999999999L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerText = findViewById(R.id.text_timer);

        //initialize the boolean so that the button is prompting the start of the timer
        mStart = true;
        mStartButton = findViewById(R.id.button_timer);
        mStartButton.setText("Start");
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });

        mCountdownTimer = new CountDownTimer(END, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerText.setText(Long.toString((END - millisUntilFinished)/1000));
            }

            @Override
            public void onFinish() {

            }
        };

    }//onCreate

    //function for handling the click
    private void buttonClick() {

        //decide weather it is stop or stop
        if (mStart) {
            mStart = false;
            mStartButton.setText("Stop");
            startTimer();
        } else {
            mStart = true;
            mStartButton.setText("Start");
            pauseTimer();
        }//if-else
    }//buttonClick

    //function for starting the timer
    private void startTimer() {
        mCountdownTimer.start();

    }//startTimer

    //function for pausing the timer
    private void pauseTimer() {
        mCountdownTimer.cancel();

    }//pauseTimer

}//main
