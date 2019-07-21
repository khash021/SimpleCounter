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


    }//onCreate

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }//onClick
}//BpmCalculatorActivity
