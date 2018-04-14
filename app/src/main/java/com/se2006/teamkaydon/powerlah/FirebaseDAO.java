package com.se2006.teamkaydon.powerlah;

import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public interface FirebaseDAO {
    FirebaseUser getCurrentUser();
    DatabaseReference getWalletValue();
    void setWalletValue(Float walletValue);
    DatabaseReference getBorrowingStatus();
    void setBorrowingStatus(boolean borrowing);
    DatabaseReference getStationChargerAmt(String stationIndex);
    void setStationChargerAmt(String stationIndex, int stationChargerAmt);

}
