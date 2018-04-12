package com.se2006.teamkaydon.powerlah;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.se2006.teamkaydon.powerlah.UserData;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UserDataManager {
    private static FirebaseAuth auth;
    private static DatabaseReference mDatabase;
    private static UserData currentUser;

    public static UserData readUser(){
        FirebaseUser user = auth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserData.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
        return currentUser;
    }

    // [START basic_write]
    public static void writeNewUser(String uid, String email, float wallet) {
        UserData user = new UserData(uid, 0);
        mDatabase.child("users").setValue(user);
    }
    // [END basic_write]

    public static void writeUserWallet(final float cashInAmount) {
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //Get Firebase auth instance
        FirebaseUser user = auth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user value
                    UserData user = dataSnapshot.getValue(UserData.class);

                    // [START_EXCLUDE]
                    if (user == null) {
                        // User is null, error out
                        Log.e(TAG, "User " + uid + " is unexpectedly null");
                    } else {
                        dataSnapshot.getRef().child("users").child(uid).child("walletValue").setValue(cashInAmount);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });
            // [END single_value_read]

        }
    }


}
