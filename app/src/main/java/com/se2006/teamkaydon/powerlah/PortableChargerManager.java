package com.se2006.teamkaydon.powerlah;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PortableChargerManager {
    private int stationChargerAmt = 20;

    public void BorrowPortable(String stationIndex){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mStationRef = mRootRef.child("chargingstation").child(stationIndex);

        mStationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stationChargerAmt = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
