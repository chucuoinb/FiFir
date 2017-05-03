package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 2/21/2017.
 */

public class Conversation{
    private int id;
    private String nameConservation;
    private String avatar;
    private String lastMessage;
    private long time;
    private boolean isNew;
    private boolean isShowCheckBox;



    private int typeChoose;



    public Conversation() {
    }
    public Conversation(int id, String nameConservation, String lastMessage, long time,boolean isNew ,int typeChoose) {
        this.id = id;
        this.nameConservation = nameConservation;
        this.lastMessage = lastMessage;
        this.time = time;
        this.isNew = isNew;
        this.isShowCheckBox = false;
        this.typeChoose = typeChoose;
    }
    public Conversation(String nameConservation,int id){
        this.nameConservation=nameConservation;
        this.id= id;
    }
    public Conversation(String nameConservation, String avatar, int id) {
        this.id = id;
        this.avatar = avatar;
        this.nameConservation=nameConservation;
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



    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public String getNameConservation() {
        return nameConservation;
    }

    public void setNameConservation(String nameConservation) {
        this.nameConservation = nameConservation;
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
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
    public int getTypeChoose() {
        return typeChoose;
    }

    public void setTypeChoose(int typeChoose) {
        this.typeChoose = typeChoose;
    }
}
