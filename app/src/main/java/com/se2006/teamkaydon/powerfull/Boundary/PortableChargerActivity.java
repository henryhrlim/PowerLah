package com.se2006.teamkaydon.powerfull.Boundary;

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
import com.se2006.teamkaydon.powerfull.Control.FirebaseManager;
import com.se2006.teamkaydon.powerfull.PaymentActivity;
import com.se2006.teamkaydon.powerfull.R;
import com.se2006.teamkaydon.powerfull.Control.TimerApp;

/**
 * Provides interface for users to borrow/return portable charger from a selected charging station.
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public class PortableChargerActivity extends AppCompatActivity {
    private int stationChargerAmt = 20;
    private int currentValue;
    private Button borrowBtn;
    private Button returnBtn;
    private TextView chargerAmt;
    private TextView paymentAmt;
    private TextView title;
    private boolean borrowing;
    FirebaseDAO firebase = new FirebaseManager();

    /**Instantiates and creates interface UI for borrowing/returning portable chargers, including display of current amount of
     * portable chargers available at the current selected charging station and borrow and return buttons.
     * @param savedInstanceState a Bundle object containing previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portable_charger);

        borrowBtn = findViewById(R.id.borrow_button);
        returnBtn = findViewById(R.id.return_button);
        chargerAmt = findViewById(R.id.charger_amt);
        paymentAmt = findViewById(R.id.payment_amt);
        title = findViewById(R.id.textView5);
        title.setText("Available chargers at " + MapsActivity.currentlySelectedMarker);

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
                borrowBtn.setVisibility(View.GONE);
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnPortable(stationIndex, TimerApp.seconds);
                TimerApp.timerAppInstance.stopTimer();
                returnBtn.setVisibility(View.GONE);
            }
        });
    }


    /**Logic for borrowing portable charger from the selected charging station, if user's wallet value is below $20, user is redirected to wallet
     * activity to top up their wallet.
     * @param stationIndex String value of the index number of the current charging station user has selected to borrow/return portable charger
     */
    public void BorrowPortable(String stationIndex){

        if(currentValue < 20){
            Toast.makeText(getBaseContext(), "Unable to borrow, wallet must have at least $20!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PortableChargerActivity.this, WalletActivity.class);
            startActivity(intent);
        }
        else if (stationChargerAmt <= 0) {
            Toast.makeText(getBaseContext(), "No more portable chargers here, please return to Main Page to select another charging station.", Toast.LENGTH_LONG).show();
        }
        else{
            borrowing = true;
            stationChargerAmt = stationChargerAmt - 1;
            firebase.setStationChargerAmt(stationIndex, stationChargerAmt);
            firebase.setBorrowingStatus(borrowing);
            Toast.makeText(getBaseContext(), "Borrowed!", Toast.LENGTH_SHORT).show();
            TimerApp.timerAppInstance.startTimer();
        }
    }

    /**
     *Logic for returning portable charger to the selected charging station, including calling method calculatePayment() to calculate the
     * payment required from the user.
     * @param stationIndex String value of the index number of the current charging station user has selected to borrow/return portable charger
     * @param usageTime Double value of the length of time user has been borrowing the portable charger for.
     */
    public void ReturnPortable(String stationIndex, double usageTime){
        borrowing = false;
        stationChargerAmt = stationChargerAmt + 1;
        firebase.setStationChargerAmt(stationIndex, stationChargerAmt);
        firebase.setBorrowingStatus(borrowing);
        calculatePayment(usageTime);
        Toast.makeText(getBaseContext(), "Returned!", Toast.LENGTH_SHORT).show();
    }

    /**
     *Logic for calculating the charge rate for borrowing the portable charger for the set amount of usage time.
     * @param usageTime Double value of the length of time user has been borrowing the portable charger for.
     */
    public void calculatePayment(double usageTime) {
        //TODO change the payment rate back after live demo
        double amt = usageTime;
        if (amt>20){
            amt = 20;
        }
        paymentAmt.setVisibility(View.VISIBLE);
        int finalValue = currentValue - (int) amt;
        if(finalValue < 0){
            finalValue = 0;
        }
        firebase.setWalletValue(finalValue);
        paymentAmt.setText("Deducted $" + Double.toString(amt) + " from your wallet.");
    }
}
