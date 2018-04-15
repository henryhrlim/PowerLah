package com.se2006.teamkaydon.powerlah;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager implements FirebaseDAO{

    @Override
    public FirebaseAuth getInstance(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth;
    }

    @Override
    public FirebaseUser getCurrentUser() {
        FirebaseUser user = getInstance().getCurrentUser();
        return user;
    }

    @Override
    public DatabaseReference getWalletValue() {
        String uid = getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("wallet");
        return ref;
    }

    @Override
    public void setWalletValue(Float walletValue){
        String uid = getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("wallet");
        ref.setValue(walletValue);
    }

    @Override
    public DatabaseReference getBorrowingStatus(){
        String uid = getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("borrowing");
        return ref;
    }

    @Override
    public void setBorrowingStatus(boolean borrowing){
        String uid = getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("borrowing");
        ref.setValue(borrowing);
    }

    @Override
    public DatabaseReference getStationChargerAmt(String stationIndex) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chargingstation").child(stationIndex);
        return ref;
    }

    @Override
    public void setStationChargerAmt(String stationIndex, int stationChargerAmt) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chargingstation").child(stationIndex);
        ref.setValue(stationChargerAmt);
    }

    @Override
    public DatabaseReference getBatteryThreshold(){
        String uid = getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("batt");
        return ref;
    }

    @Override
    public void setBatteryThreshold(int batteryThreshold){
        String uid = getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("batt");
        ref.setValue(batteryThreshold);
    }

}
