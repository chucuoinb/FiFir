package com.example.nam.minisn.ItemListview;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class Conversation{
    private int id;
    private String nameConservation;
    private String avatar;



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String lastMessage;
    private String time;
    private boolean isNew;

    public Conversation(int id, String nameConservation, String lastMessage, String time,boolean isNew) {
        this.id = id;
        this.nameConservation = nameConservation;
        this.lastMessage = lastMessage;
        this.time = time;
        this.isNew = isNew;
    }

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
    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
