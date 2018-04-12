package com.se2006.teamkaydon.powerlah;

public class UserData {
    public String uid;
    public float walletValue;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(UserData.class)
    }

    public UserData(String uid, int walletValue) {
        this.uid = uid;
        this.walletValue = walletValue;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getWalletValue() {
        return walletValue;
    }

    public void setWalletValue(float walletValue) {
        this.walletValue = walletValue;
    }


}
