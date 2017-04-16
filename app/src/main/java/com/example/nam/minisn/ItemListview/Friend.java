package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 2/21/2017.
 */

public class Friend {
    private String username;
    private String displayName;
    private int gender;

    public Friend(String username, String displayName, int gender) {
        this.username = username;
        this.displayName = displayName;
        this.gender = gender;
    }

    public Friend() {

    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
