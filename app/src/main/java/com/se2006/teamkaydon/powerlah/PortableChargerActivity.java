package com.se2006.teamkaydon.powerlah;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PortableChargerActivity extends AppCompatActivity {
    private int stationChargerAmt = 20;
    private Button borrowBtn;
    private Button returnBtn;
    private TextView chargerAmt;
    private boolean borrowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portable_charger);

        borrowBtn = findViewById(R.id.borrow_button);
        returnBtn = findViewById(R.id.return_button);
        chargerAmt = findViewById(R.id.charger_amt);

        if(borrowing){
           returnBtn.setVisibility(View.GONE);
        }
        else{
            borrowBtn.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String stationIndex = bundle.getString("stationIndex");
        Log.d("PortableChargerActivity", "the station index is " + stationIndex);

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mStationRef = mRootRef.child("chargingstation").child("37");

        mStationRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long stationChargerAmtLong = dataSnapshot.getValue(Long.class);
                stationChargerAmt = Integer.valueOf(Math.toIntExact(stationChargerAmtLong));
                chargerAmt.setText(Integer.toString(stationChargerAmt));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        borrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BorrowPortable(mStationRef);
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnPortable(mStationRef);
            }
        });
    }


    public void BorrowPortable(DatabaseReference mStationRef){
        stationChargerAmt = stationChargerAmt - 1;
        mStationRef.setValue(stationChargerAmt);
        borrowBtn.setVisibility(View.GONE);
        returnBtn.setVisibility(View.VISIBLE);
    }

    public void ReturnPortable(DatabaseReference mStationRef){
        stationChargerAmt = stationChargerAmt + 1;
        mStationRef.setValue(stationChargerAmt);
        returnBtn.setVisibility(View.GONE);
        borrowBtn.setVisibility(View.VISIBLE);
    }
}
