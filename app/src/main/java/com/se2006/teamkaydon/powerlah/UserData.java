package com.se2006.teamkaydon.powerlah;

public class UserData {
    public String uid;
    public String email;
    public String password;
    public int walletValue;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(UserData.class)
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWalletValue() {
        return walletValue;
    }

    public void setWalletValue(int walletValue) {
        this.walletValue = walletValue;
    }

    public UserData(String uid, String email, String password, int walletValue) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this. walletValue = walletValue;
    }
}
