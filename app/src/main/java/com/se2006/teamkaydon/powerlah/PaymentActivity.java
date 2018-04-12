package com.se2006.teamkaydon.powerlah;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private TextView currentAmount;
    private EditText payAmount;
    private Button payButton;
    private Float walletValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        payButton = (Button) findViewById(R.id.payButton);
        payAmount = (EditText) findViewById(R.id.payAmount);
        currentAmount = (TextView) findViewById(R.id.currentAmount);


        //display current wallet amount
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mWalletRef = mRootRef.child("users").child(uid);

        mWalletRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                walletValue = dataSnapshot.getValue(Float.class);
                String sWallet = Float.toString(walletValue);
                currentAmount.setText(sWallet);
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

                Float totalValue = walletValue + Float.parseFloat(amt);
                mWalletRef.setValue(totalValue);

            }
        });
    }
}
