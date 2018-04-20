package com.se2006.teamkaydon.powerfull.Boundary;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Data Access Object that provides a list of functions for different classes to access
 * data in firebase.
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public interface FirebaseDAO {
    FirebaseAuth getInstance();
    FirebaseUser getCurrentUser();
    void setNewUser(FirebaseUser user);
    DatabaseReference getWalletValue();
    void setWalletValue(int walletValue);
    DatabaseReference getBorrowingStatus();
    void setBorrowingStatus(boolean borrowing);
    DatabaseReference getStationChargerAmt(String stationIndex);
    void setStationChargerAmt(String stationIndex, int stationChargerAmt);
    DatabaseReference getBatteryThreshold();
    void setBatteryThreshold(int batteryThreshold);
}
