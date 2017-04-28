package com.example.nam.minisn.ItemListview;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class Conversation implements Serializable{
    private int id;
    private String nameConservation;
    private String avatar;
    private boolean isNew;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


    public Conversation() {
    }

    public String getNameConservation() {
        return nameConservation;
    }
    public Conversation(String nameConservation,int id){
        this.nameConservation=nameConservation;
        this.id= id;
    }
    public void setNameConservation(String nameConservation) {
        this.nameConservation = nameConservation;
    }

    public Conversation(String nameConservation, String avatar, int id) {
        this.id = id;
        this.avatar = avatar;
        this.nameConservation=nameConservation;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
