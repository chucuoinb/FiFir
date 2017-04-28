package com.example.nam.minisn.ItemListview;

import java.io.Serializable;

/**
 * Created by Nam on 2/21/2017.
 */

public class Friend implements Serializable{

    private int id;
    private String username = new String();
    private String displayName = new String();
    private int gender;

    public Friend(String username, String displayName, int gender) {
        this.username = username;
        this.displayName = displayName;
        this.gender = gender;
    }
    public Friend(int id,String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
        this.id=id;
    }
    public Friend(String username, String displayName, int gender,int id) {
        this.username = username;
        this.displayName = displayName;
        this.gender = gender;
        this.id=id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
