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

import static android.content.ContentValues.TAG;

public class UserDataManager {
    private FirebaseAuth auth;
    private static DatabaseReference mDatabase;

    // [START basic_write]
    public static void writeNewUser(String uid, String email, int wallet) {
        UserData user = new UserData(uid, email, 0);
        mDatabase.child("users").child(uid).setValue(user);
    }
    // [END basic_write]

    public void writeData() {
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
                        Toast.makeText(NewPostActivity.this,
                                "Error: could not fetch user.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Write new post
                        writeNewPost(userId, user.username, title, body);
                    }
                    ;
                    // Finish this Activity, back to the stream
                    setEditingEnabled(true);
                    finish();
                    // [END_EXCLUDE]
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    setEditingEnabled(true);
                    // [END_EXCLUDE]
                }
            });
            // [END single_value_read]

        }

        public void readData(){
            //Get Firebase auth instance
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
            }
        }
    }
}
