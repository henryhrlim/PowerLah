package com.se2006.teamkaydon.powerlah;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class BatteryActivity extends AppCompatActivity {
    private static int battThreshold = 10;             //default is 10

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        SeekBar seekBar;
        seekBar = findViewById(R.id.seekBarBatt);

        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                battThreshold = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(),"Threshold is now set to " + battThreshold + "%", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static int getThreshold(){
        return battThreshold;
    }

}
