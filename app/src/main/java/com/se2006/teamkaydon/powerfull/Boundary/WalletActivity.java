package com.se2006.teamkaydon.powerfull.Boundary;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.se2006.teamkaydon.powerfull.Control.FirebaseManager;
import com.se2006.teamkaydon.powerfull.PaymentActivity;
import com.se2006.teamkaydon.powerfull.R;

/**
 * Interface for user's wallet, retrives and shows user's wallet value and provide methods for user to top up wallet value.
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public class WalletActivity extends AppCompatActivity {

    private TextView currentAmount;
    private EditText payAmount;
    private Button payButton;
    private int walletValue;
    private int totalValue;

    FirebaseDAO firebase = new FirebaseManager();

    /**Instantiates UI elements such as the wallet value textview and the top up wallet button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.wallet_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        payButton = (Button) findViewById(R.id.payButton);
        payAmount = (EditText) findViewById(R.id.payAmount);
        currentAmount = (TextView) findViewById(R.id.currentAmount);

        //display current wallet amount
        firebase.getWalletValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                walletValue = dataSnapshot.getValue(int.class);
                currentAmount.setText(String.valueOf(walletValue));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amt = String.valueOf(payAmount.getText());

                if (TextUtils.isEmpty(amt)) {
                    Toast.makeText(getApplicationContext(), "Enter an amount to top up!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(WalletActivity.this, PaymentActivity.class);
                startActivityForResult(intent, 0);

                totalValue = walletValue + Integer.valueOf(amt);
            }
        });


    }

    /** Method to run statements returning from previously called Activity from Intent requestCode
     * @param requestCode Integer object for requestCode from passed Intent
     * @param resultCode Integer object for resultCode from passed Intent
     * @param data Intent object
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK){
                firebase.setWalletValue(totalValue);
                Toast.makeText(WalletActivity.this, "Payment successful!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(WalletActivity.this, "Payment failed!", Toast.LENGTH_SHORT).show();
            }
        }
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
}
