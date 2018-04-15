package com.se2006.teamkaydon.powerlah;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class BatteryActivity extends AppCompatActivity {
    private static int battThreshold;             //default is 10

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.battery_toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        final SeekBar seekBar;
        final TextView currentThreshold;
        seekBar = findViewById(R.id.seekBarBatt);
        currentThreshold = findViewById(R.id.currentThreshold);

        seekBar.setMax(100);
        final FirebaseDAO firebase = new FirebaseManager();
        firebase.getBatteryThreshold().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                battThreshold = dataSnapshot.getValue(int.class);
                seekBar.setProgress(battThreshold);
                currentThreshold.setText(battThreshold + "%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                battThreshold = i;
                currentThreshold.setText(battThreshold + "%");
                firebase.setBatteryThreshold(battThreshold);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getThreshold(){
        return battThreshold;
    }

}
