package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 2/21/2017.
 */

public class Chat {
    private String message;
    private int gender;
    private int typeMessage;
    private int idSend;
    public Chat() {
    }

    public Chat(int typeMessage, String message, int gender,int id) {
        this.typeMessage = typeMessage;
        this.message = message;
        this.gender = gender;
        this.idSend =id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getTypeMessage() {
        return typeMessage;
    }

    public void setMyMessage(int typeMessage) {
        this.typeMessage = typeMessage;
    }

    public int getIdSend() {
        return idSend;
    }

    public void setIdSend(int idSend) {
        this.idSend = idSend;
    }
}
