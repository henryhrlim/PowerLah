package com.se2006.teamkaydon.powerlah.Boundary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.se2006.teamkaydon.powerlah.Control.FirebaseManager;
import com.se2006.teamkaydon.powerlah.PaymentActivity;
import com.se2006.teamkaydon.powerlah.R;
import com.se2006.teamkaydon.powerlah.Control.TimerApp;

public class PortableChargerActivity extends AppCompatActivity {
    private int stationChargerAmt = 20;
    private int currentValue;
    private Button borrowBtn;
    private Button returnBtn;
    private TextView chargerAmt;
    private TextView paymentAmt;
    private boolean borrowing;
    FirebaseDAO firebase = new FirebaseManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portable_charger);

        borrowBtn = findViewById(R.id.borrow_button);
        returnBtn = findViewById(R.id.return_button);
        chargerAmt = findViewById(R.id.charger_amt);
        paymentAmt = findViewById(R.id.payment_amt);

        paymentAmt.setVisibility(View.GONE);
        firebase.getBorrowingStatus().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(boolean.class) == true){
                    borrowBtn.setVisibility(View.GONE);
                    returnBtn.setVisibility(View.VISIBLE);
                    paymentAmt.setVisibility(View.GONE);
                }
                else{
                    returnBtn.setVisibility(View.GONE);
                    borrowBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        firebase.getWalletValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentValue = dataSnapshot.getValue(int.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String stationIndex = bundle.getString("stationIndex");

        firebase.getStationChargerAmt(stationIndex).addValueEventListener(new ValueEventListener() {
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
                BorrowPortable(stationIndex);
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnPortable(stationIndex, TimerApp.seconds);
                TimerApp.timerAppInstance.stopTimer();
            }
        });
    }


    public void BorrowPortable(String stationIndex){

        if(currentValue < 20){
            Toast.makeText(getBaseContext(), "Unable to borrow, wallet must have at least $20!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PortableChargerActivity.this, WalletActivity.class);
            startActivity(intent);
        }
        else{
            borrowing = true;
            stationChargerAmt = stationChargerAmt - 1;
            firebase.setStationChargerAmt(stationIndex, stationChargerAmt);
            firebase.setBorrowingStatus(borrowing);
            Toast.makeText(getBaseContext(), "Borrowed!", Toast.LENGTH_LONG).show();
            TimerApp.timerAppInstance.startTimer();
        }
    }

    public void ReturnPortable(String stationIndex, double usageTime){
        borrowing = false;
        stationChargerAmt = stationChargerAmt + 1;
        firebase.setStationChargerAmt(stationIndex, stationChargerAmt);
        firebase.setBorrowingStatus(borrowing);
        calculatePayment(usageTime);
        Toast.makeText(getBaseContext(), "Returned!", Toast.LENGTH_LONG).show();
    }

    public void calculatePayment(double usageTime) {
        final double amt = usageTime * 10;
        paymentAmt.setVisibility(View.VISIBLE);
        int finalValue = currentValue - (int) amt;
        firebase.setWalletValue(finalValue);
        paymentAmt.setText("Deducted $" + Double.toString(amt) + "from your wallet.");
    }
}
